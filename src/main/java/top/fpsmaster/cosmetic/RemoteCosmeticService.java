package top.fpsmaster.cosmetic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.modules.client.api.AuthService;
import top.fpsmaster.modules.client.api.FPSMasterApiClient;
import top.fpsmaster.modules.client.api.model.ApiResponse;
import top.fpsmaster.modules.client.api.model.CosmeticItem;
import top.fpsmaster.modules.client.api.model.CosmeticLoadoutView;
import top.fpsmaster.modules.client.api.model.ResolvedLoadoutView;
import top.fpsmaster.modules.logger.ClientLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Publishes this account's cosmetic loadout and resolves everyone else's, so two FPSMaster players on
 * the same server see each other's cosmetics.
 *
 * <p>Reads are a batch resolve keyed by Minecraft UUID, scheduled by {@link ResolveScheduler}: every
 * answer (including "this player has nothing") is trusted for five minutes, a pass runs at most once a
 * second and asks only for players without a fresh answer, and failures back off exponentially. The
 * whole cache is dropped on world leave or logout, so a stale loadout never outlives the session that
 * produced it.
 *
 * <p>Writes are deliberately asymmetric: equipping or toggling pushes at once because it is a discrete
 * decision, while the scale slider debounces so dragging it does not become one request per frame.
 * Nothing here reports success it did not observe — a failed push leaves local rendering exactly as it
 * was and moves {@link #status()} to {@link Status#FAILED}, which the cosmetics screen shows.
 */
public final class RemoteCosmeticService {
    private static final long PUSH_DEBOUNCE_MS = 500L;

    private static final RemoteCosmeticService INSTANCE = new RemoteCosmeticService();

    public enum Status {
        /** Nothing pushed yet this session. */
        IDLE,
        SYNCING,
        SYNCED,
        /** The push failed; local rendering is unchanged and nothing was stored server-side. */
        FAILED,
        /** No account, an offline session, or a backend without the loadout endpoint. */
        UNAVAILABLE
    }

    private final ResolveScheduler<RemoteLoadout> resolver = new ResolveScheduler<>();
    private final AtomicBoolean pushInFlight = new AtomicBoolean(false);

    private volatile boolean hadWorld;

    private volatile long pushDueAt;
    private volatile boolean pushPending;
    private volatile Status status = Status.IDLE;
    private volatile String statusMessage = "";
    /** Set once any loadout round-trip succeeds; see {@link #syncStatusKey()}. */
    private volatile boolean everSucceeded;
    private volatile CompletableFuture<?> lastPush;

    private RemoteCosmeticService() {
    }

    public static RemoteCosmeticService getInstance() {
        return INSTANCE;
    }

    public Status status() {
        return status;
    }

    public String statusMessage() {
        return statusMessage;
    }

    /**
     * The cosmetics screen footer, in Prism's vocabulary: {@code ok}, {@code failed} or
     * {@code unavailable}.
     *
     * <p>This answers "does cloud sync work for this account", not "has the last write landed" —
     * so an in-flight push after an earlier success stays {@code ok} rather than flickering through
     * {@code unavailable}. It only ever reports {@code ok} once a round-trip has actually succeeded;
     * having pushed nothing yet is {@code unavailable}, never an optimistic success.
     */
    public String syncStatusKey() {
        if (status == Status.FAILED) {
            return "failed";
        }
        if (status == Status.SYNCED || everSucceeded && status == Status.SYNCING) {
            return "ok";
        }
        return "unavailable";
    }

    // ================== Reading other players ================== //

    /** The cached loadout for a Minecraft UUID, or null when unknown, expired or resolved to nothing. */
    public RemoteLoadout loadoutFor(UUID uuid) {
        return resolver.get(uuid, System.currentTimeMillis());
    }

    /**
     * Called every client tick (20 Hz). Only the debounced push and the world-leave check run at that
     * rate; resolving is throttled and batched by {@link ResolveScheduler}.
     */
    public void onClientTick() {
        Minecraft minecraft = Minecraft.getMinecraft();
        boolean hasWorld = minecraft.theWorld != null && minecraft.getNetHandler() != null;
        if (hadWorld && !hasWorld) {
            clearRemote();
        }
        hadWorld = hasWorld;

        flushDuePush();

        if (!hasWorld || !AuthService.getInstance().isLoggedIn()) {
            return;
        }
        // Linking is what makes this player visible to others; retry it here so a failed attempt
        // (network down at startup) recovers without a restart. linkIfNeeded paces itself.
        MinecraftLinkService.getInstance().linkIfNeeded();

        long now = System.currentTimeMillis();
        if (!resolver.passDue(now)) {
            return;
        }
        ResolveScheduler.Batch batch = resolver.nextBatch(onlinePlayers(minecraft), now);
        if (batch != null) {
            resolve(batch);
        }
    }

    private Set<UUID> onlinePlayers(Minecraft minecraft) {
        Set<UUID> online = new LinkedHashSet<>();
        Collection<NetworkPlayerInfo> infos = minecraft.getNetHandler().getPlayerInfoMap();
        if (infos == null) {
            return online;
        }
        for (NetworkPlayerInfo info : infos) {
            if (info == null || info.getGameProfile() == null || info.getGameProfile().getId() == null) {
                continue;
            }
            online.add(info.getGameProfile().getId());
        }
        return online;
    }

    private void resolve(ResolveScheduler.Batch batch) {
        List<String> uuids = new ArrayList<String>(batch.uuids.size());
        for (UUID uuid : batch.uuids) {
            // UUID.toString() is already the canonical dashed lowercase form the endpoint requires;
            // it rejects anything else.
            uuids.add(uuid.toString());
        }
        FPSMasterApiClient.getInstance().resolveLoadouts(uuids).whenComplete((response, error) -> {
            long now = System.currentTimeMillis();
            if (error != null || response == null || !response.isSuccess() || response.getData() == null) {
                // A resolve failure degrades to "nobody else has cosmetics", never to a wrong loadout,
                // so it does not touch this account's own sync status. It does back off: a backend
                // without the endpoint (404) or one that is down must not be asked again next second.
                long delay = resolver.onFailure(now);
                ClientLogger.warn("Cosmetic resolve failed (" + describe(response, error) + "), retrying in "
                        + (delay / 1000L) + "s");
                return;
            }
            Map<UUID, RemoteLoadout> found = new HashMap<>();
            try {
                for (ResolvedLoadoutView entry : response.getData()) {
                    if (entry == null || entry.getLoadout() == null) {
                        continue;
                    }
                    UUID uuid = parseUuid(entry.getMinecraftUuid());
                    if (uuid != null) {
                        found.put(uuid, toLoadout(entry.getLoadout()));
                    }
                }
            } catch (RuntimeException exception) {
                // A malformed entry poisons this answer only; caching what parsed so far as "nothing"
                // would hide real loadouts for five minutes, so treat it like a failed request.
                resolver.onFailure(now);
                ClientLogger.warn("Failed to apply resolved cosmetic loadouts: " + exception.getMessage());
                return;
            }
            resolver.onSuccess(batch, found, now);
        });
    }

    private static String describe(ApiResponse<?> response, Throwable error) {
        if (error != null) {
            return error.getClass().getSimpleName();
        }
        if (response == null) {
            return "no response";
        }
        String code = response.getErrorCode();
        return "HTTP " + response.getCode() + (code == null ? "" : " " + code);
    }

    private RemoteLoadout toLoadout(CosmeticLoadoutView view) {
        CosmeticManager cosmetics = CosmeticManager.getInstance();
        String capeId = register(cosmetics, view.getCapeItem(), view.getCapeItemId());
        String backId = register(cosmetics, view.getBackItem(), view.getBackItemId());
        String backCategory = view.getBackItem() != null
                ? view.getBackItem().getCategory().toLowerCase(Locale.ROOT) : "wings";
        return new RemoteLoadout(capeId, backId, backCategory,
                view.isBuiltinWingsEnabled(), view.getWingScale(), view.isCapeAnimationEnabled());
    }

    private String register(CosmeticManager cosmetics, CosmeticItem item, String fallbackId) {
        return item == null ? fallbackId : cosmetics.registerRemoteOption(item);
    }

    private static UUID parseUuid(String raw) {
        if (raw == null) {
            return null;
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    // ================== Publishing this account ================== //

    /** Equip and toggle changes: discrete decisions, pushed immediately. */
    public void publishNow() {
        schedule(0L);
    }

    /** Slider drags: coalesced so one gesture becomes one request. */
    public void publishDebounced() {
        schedule(PUSH_DEBOUNCE_MS);
    }

    /** Screen close: sends the final value rather than losing it to the debounce. */
    public void flush() {
        if (pushPending) {
            pushDueAt = 0L;
            flushDuePush();
        }
    }

    /**
     * Shutdown variant. The HTTP call runs on a daemon pool that the JVM will not wait for, so a
     * pending scale change would simply vanish when the game closes; this gives it a bounded moment
     * to land and then gives up rather than holding up the exit.
     */
    public void flushBlocking(long timeoutMs) {
        flush();
        CompletableFuture<?> push = lastPush;
        if (push == null) {
            return;
        }
        try {
            push.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception exception) {
            ClientLogger.warn("Cosmetic loadout was not flushed before shutdown: "
                    + exception.getClass().getSimpleName());
        }
    }

    private void schedule(long delayMs) {
        if (!AuthService.getInstance().isLoggedIn()) {
            status = Status.UNAVAILABLE;
            statusMessage = FPSMaster.i18n.get("cosmetics.sync.signedout");
            return;
        }
        // An offline session has no verifiable Minecraft identity to attach a loadout to, so there is
        // nothing to publish. The chosen cosmetics still render locally.
        if (!MinecraftLinkService.isOnlineSession()) {
            status = Status.UNAVAILABLE;
            statusMessage = FPSMaster.i18n.get("cosmetics.sync.offline");
            return;
        }
        pushPending = true;
        long due = System.currentTimeMillis() + delayMs;
        // An immediate push must not inherit an earlier debounced deadline.
        if (pushDueAt == 0L || due < pushDueAt) {
            pushDueAt = due;
        }
        if (delayMs == 0L) {
            flushDuePush();
        }
    }

    private void flushDuePush() {
        if (!pushPending || System.currentTimeMillis() < pushDueAt) {
            return;
        }
        if (!pushInFlight.compareAndSet(false, true)) {
            return;
        }
        pushPending = false;
        pushDueAt = 0L;
        status = Status.SYNCING;
        statusMessage = FPSMaster.i18n.get("cosmetics.sync.pending");

        CosmeticManager cosmetics = CosmeticManager.getInstance();
        CosmeticManager.CosmeticOption cape = cosmetics.selectedCape();
        CosmeticManager.CosmeticOption back = cosmetics.selectedWings();
        boolean builtin = CosmeticManager.BUILTIN_WINGS_ID.equals(back.getId());
        String capeId = remoteId(cape == null ? null : cape.getId());
        String backId = builtin ? null : remoteId(back.getId());
        boolean builtinEnabled = builtin && cosmetics.wingsEnabled();
        float wingScale = cosmetics.storedWingScale();
        boolean capeAnimation = cosmetics.capeAnimationEnabled();

        lastPush = FPSMasterApiClient.getInstance()
                .putCosmeticLoadout(capeId, backId, builtinEnabled, wingScale, capeAnimation)
                .whenComplete((response, error) -> {
                    pushInFlight.set(false);
                    if (error == null && response != null && response.isSuccess()) {
                        everSucceeded = true;
                        status = Status.SYNCED;
                        statusMessage = FPSMaster.i18n.get("cosmetics.sync.ok");
                        return;
                    }
                    // A backend without the endpoint answers 404: that is "unavailable", not "failed".
                    // Either way the local loadout keeps rendering exactly as it was chosen.
                    boolean missing = error == null && response != null && response.getCode() == 404;
                    status = missing ? Status.UNAVAILABLE : Status.FAILED;
                    // The backend's message is localized via Accept-Language ("你还没有拥有该物品"), so
                    // it is worth surfacing; anything else (network, parse) gets the generic line.
                    String message = error != null ? error.getClass().getSimpleName()
                            : response == null ? "no response" : describe(response, null);
                    statusMessage = !missing && error == null && response != null && response.hasServerMessage()
                            ? response.getMessage()
                            : FPSMaster.i18n.get(missing ? "cosmetics.sync.unavailable" : "cosmetics.sync.failed");
                    ClientLogger.warn("Cosmetic loadout sync failed: " + message);
                });
    }

    /**
     * Locally authored cosmetics have no server-side identity, so they publish as "nothing in that
     * slot" rather than as an id the backend would reject.
     */
    private static String remoteId(String id) {
        return id == null || id.startsWith("custom:") || id.startsWith("builtin:") ? null : id;
    }

    // ================== Lifecycle ================== //

    /** World leave: other players' loadouts stop being true the moment we stop sharing a server. */
    public void clearRemote() {
        resolver.clearCache();
    }

    /** Logout: everything remote goes, including the sync status of an account we no longer hold. */
    public void onLogout() {
        resolver.reset();
        pushPending = false;
        pushDueAt = 0L;
        status = Status.IDLE;
        statusMessage = "";
        everSucceeded = false;
        MinecraftLinkService.getInstance().reset();
    }

    /** Pulls this account's stored loadout so a second client starts from the account, not from disk. */
    public void pullOwnLoadout() {
        if (!AuthService.getInstance().isLoggedIn()) {
            return;
        }
        FPSMasterApiClient.getInstance().getCosmeticLoadout().whenComplete((response, error) -> {
            if (error != null || response == null || !response.isSuccess() || response.getData() == null) {
                return;
            }
            everSucceeded = true;
            status = Status.SYNCED;
            statusMessage = FPSMaster.i18n.get("cosmetics.sync.ok");
            CosmeticLoadoutView view = response.getData();
            Minecraft.getMinecraft().addScheduledTask(
                    () -> CosmeticManager.getInstance().applyAccountLoadout(view));
        });
    }

    /** A resolved loadout in the terms the renderers need: cosmetic option ids and a real scale. */
    public static final class RemoteLoadout {
        public final String capeItemId;
        public final String backItemId;
        public final String backCategory;
        public final boolean builtinWingsEnabled;
        public final float wingScale;
        public final boolean capeAnimationEnabled;

        RemoteLoadout(String capeItemId, String backItemId, String backCategory,
                      boolean builtinWingsEnabled, float wingScale, boolean capeAnimationEnabled) {
            this.capeItemId = capeItemId;
            this.backItemId = backItemId;
            this.backCategory = backCategory;
            this.builtinWingsEnabled = builtinWingsEnabled;
            this.wingScale = wingScale;
            this.capeAnimationEnabled = capeAnimationEnabled;
        }
    }
}
