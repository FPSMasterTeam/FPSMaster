package top.fpsmaster.cosmetic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Decides when other players' loadouts are asked for, so the resolve endpoint sees a handful of
 * requests per session instead of one per tick.
 *
 * <ul>
 *   <li>A player the backend answered for is cached for {@link #ENTRY_TTL_MS} — including the
 *       answer "no loadout", which is what nearly every player on a public server gets. Without that
 *       negative entry they were re-requested as soon as the previous request finished.</li>
 *   <li>At most one request is in flight and a pass runs at most every {@link #PASS_INTERVAL_MS};
 *       everything a pass finds missing goes into one batch of at most {@link #MAX_BATCH}.</li>
 *   <li>A failed request backs off exponentially from {@link #BACKOFF_BASE_MS} up to
 *       {@link #BACKOFF_MAX_MS}; one success resets it.</li>
 * </ul>
 *
 * <p>Pure bookkeeping with an injected clock, so the schedule is unit-testable without a game.
 *
 * @param <V> what a resolved player maps to; null means "resolved, has nothing"
 */
final class ResolveScheduler<V> {
    static final int MAX_BATCH = 200;
    static final long ENTRY_TTL_MS = 5L * 60_000L;
    static final long PASS_INTERVAL_MS = 1_000L;
    static final long BACKOFF_BASE_MS = 5_000L;
    static final long BACKOFF_MAX_MS = 5L * 60_000L;

    /** A request the caller must send; hand it back to {@link #onSuccess} or {@link #onFailure}. */
    static final class Batch {
        final List<UUID> uuids;
        private final int generation;

        private Batch(List<UUID> uuids, int generation) {
            this.uuids = Collections.unmodifiableList(uuids);
            this.generation = generation;
        }
    }

    private static final class Entry<V> {
        final V value;
        final long fetchedAt;

        Entry(V value, long fetchedAt) {
            this.value = value;
            this.fetchedAt = fetchedAt;
        }
    }

    /** Read every frame by the renderers, written by the HTTP callback: hence concurrent. */
    private final Map<UUID, Entry<V>> cache = new ConcurrentHashMap<>();
    private boolean inFlight;
    private long lastPassAt = Long.MIN_VALUE / 2;
    private int failures;
    private long retryAt;
    /** Bumped by {@link #clearCache()}; answers to requests sent before it are dropped. */
    private int generation;

    /** The cached value, or null when unknown, expired, or resolved to nothing. */
    V get(UUID uuid, long now) {
        Entry<V> entry = uuid == null ? null : cache.get(uuid);
        if (entry == null || now - entry.fetchedAt > ENTRY_TTL_MS) {
            return null;
        }
        return entry.value;
    }

    /** Whether {@link #nextBatch} could send anything now; lets the caller skip walking the tab list. */
    synchronized boolean passDue(long now) {
        return !inFlight && now >= retryAt && now - lastPassAt >= PASS_INTERVAL_MS;
    }

    /** The batch to send now, or null when nothing is due (throttled, backing off, busy or all known). */
    synchronized Batch nextBatch(Collection<UUID> online, long now) {
        if (!passDue(now)) {
            return null;
        }
        lastPassAt = now;
        List<UUID> wanted = new ArrayList<>();
        for (UUID uuid : online) {
            if (wanted.size() >= MAX_BATCH) {
                break;
            }
            Entry<V> entry = cache.get(uuid);
            if (entry == null || now - entry.fetchedAt > ENTRY_TTL_MS) {
                wanted.add(uuid);
            }
        }
        if (wanted.isEmpty()) {
            return null;
        }
        inFlight = true;
        return new Batch(wanted, generation);
    }

    /**
     * Records the answer. Every requested player gets an entry: the ones absent from {@code found}
     * are cached as "nothing", which is what keeps them from being asked for again next pass.
     */
    synchronized void onSuccess(Batch batch, Map<UUID, V> found, long now) {
        inFlight = false;
        failures = 0;
        retryAt = 0L;
        if (batch.generation != generation) {
            return;
        }
        for (UUID uuid : batch.uuids) {
            cache.put(uuid, new Entry<>(found.get(uuid), now));
        }
    }

    /** Backs off: 5s, 10s, 20s ... capped at five minutes. Returns the delay chosen. */
    synchronized long onFailure(long now) {
        inFlight = false;
        failures++;
        long delay = BACKOFF_BASE_MS << Math.min(failures - 1, 20);
        delay = Math.min(delay, BACKOFF_MAX_MS);
        retryAt = now + delay;
        return delay;
    }

    /**
     * World leave: the cache stops being true, but the backoff stays — a backend that was failing a
     * second ago is not healthier because we changed servers.
     */
    synchronized void clearCache() {
        cache.clear();
        generation++;
        lastPassAt = Long.MIN_VALUE / 2;
    }

    /** Logout: everything goes. */
    synchronized void reset() {
        clearCache();
        failures = 0;
        retryAt = 0L;
    }
}
