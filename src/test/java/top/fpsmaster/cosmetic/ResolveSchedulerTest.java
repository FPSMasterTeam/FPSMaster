package top.fpsmaster.cosmetic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResolveSchedulerTest {
    private final UUID a = UUID.randomUUID();
    private final UUID b = UUID.randomUUID();
    private final List<UUID> online = Arrays.asList(a, b);

    @Test
    void playersWithoutLoadoutAreNotRequestedAgainUntilTtl() {
        ResolveScheduler<String> scheduler = new ResolveScheduler<>();
        long now = 1_000_000L;
        ResolveScheduler.Batch first = scheduler.nextBatch(online, now);
        assertNotNull(first);
        assertEquals(online, first.uuids);

        Map<UUID, String> found = new HashMap<>();
        found.put(a, "cape");
        scheduler.onSuccess(first, found, now);

        assertEquals("cape", scheduler.get(a, now));
        assertNull(scheduler.get(b, now));
        // Every tick for the next five minutes: nothing to ask for, b is negatively cached.
        for (long t = now + 50L; t < now + ResolveScheduler.ENTRY_TTL_MS; t += 50L * 97L) {
            assertNull(scheduler.nextBatch(online, t));
        }
        ResolveScheduler.Batch expired = scheduler.nextBatch(online, now + ResolveScheduler.ENTRY_TTL_MS + 1L);
        assertNotNull(expired);
    }

    @Test
    void passesAreThrottledAndSingleFlight() {
        ResolveScheduler<String> scheduler = new ResolveScheduler<>();
        long now = 5_000_000L;
        ResolveScheduler.Batch batch = scheduler.nextBatch(Collections.singletonList(a), now);
        assertNotNull(batch);
        // In flight: nothing else goes out, even for a new player and long after the interval.
        assertNull(scheduler.nextBatch(online, now + 10_000L));
        scheduler.onSuccess(batch, Collections.emptyMap(), now + 10_000L);
        // Throttle: a newcomer seen within the same second waits for the next pass.
        ResolveScheduler.Batch next = scheduler.nextBatch(online, now + 10_000L);
        assertNotNull(next);
        assertEquals(Collections.singletonList(b), next.uuids);
        scheduler.onSuccess(next, Collections.emptyMap(), now + 10_000L);
        UUID c = UUID.randomUUID();
        assertNull(scheduler.nextBatch(Arrays.asList(a, b, c), now + 10_500L));
        assertNotNull(scheduler.nextBatch(Arrays.asList(a, b, c), now + 11_000L));
    }

    @Test
    void failuresBackOffExponentiallyUpToFiveMinutes() {
        ResolveScheduler<String> scheduler = new ResolveScheduler<>();
        long now = 9_000_000L;
        long[] expected = {5_000L, 10_000L, 20_000L, 40_000L, 80_000L, 160_000L, 300_000L, 300_000L};
        for (long delay : expected) {
            assertNotNull(scheduler.nextBatch(online, now));
            assertEquals(delay, scheduler.onFailure(now));
            assertNull(scheduler.nextBatch(online, now + delay - 1L));
            now += delay;
        }
        ResolveScheduler.Batch batch = scheduler.nextBatch(online, now);
        assertNotNull(batch);
        scheduler.onSuccess(batch, Collections.emptyMap(), now);
        // Success resets the backoff.
        assertNotNull(scheduler.nextBatch(Collections.singletonList(UUID.randomUUID()), now + 1_000L));
        assertEquals(5_000L, scheduler.onFailure(now + 1_000L));
    }

    @Test
    void batchesAreCappedAndAnswersFromBeforeAWorldChangeAreDropped() {
        ResolveScheduler<String> scheduler = new ResolveScheduler<>();
        List<UUID> crowd = new ArrayList<>();
        for (int i = 0; i < 450; i++) {
            crowd.add(UUID.randomUUID());
        }
        long now = 20_000_000L;
        ResolveScheduler.Batch batch = scheduler.nextBatch(crowd, now);
        assertNotNull(batch);
        assertEquals(ResolveScheduler.MAX_BATCH, batch.uuids.size());

        scheduler.clearCache();
        Map<UUID, String> found = new HashMap<>();
        found.put(crowd.get(0), "wings");
        scheduler.onSuccess(batch, found, now);
        assertNull(scheduler.get(crowd.get(0), now));
        // The in-flight slot is free again and the whole crowd is unknown.
        ResolveScheduler.Batch again = scheduler.nextBatch(crowd, now);
        assertNotNull(again);
        assertEquals(crowd.subList(0, ResolveScheduler.MAX_BATCH), again.uuids);
    }
}
