package top.fpsmaster.cosmetic;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import net.minecraft.client.Minecraft;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.modules.client.api.AuthService;
import top.fpsmaster.modules.client.api.FPSMasterApiClient;
import top.fpsmaster.modules.client.api.model.ApiResponse;
import top.fpsmaster.modules.client.api.model.MinecraftLinkChallenge;
import top.fpsmaster.modules.logger.ClientLogger;

import java.util.UUID;

/**
 * Proves ownership of the running Minecraft account to the FPSMaster backend, which is what makes this
 * player visible to other FPSMaster clients on the same server.
 *
 * <p>The client never tells the backend its UUID. It asks for a challenge, joins Mojang's session
 * server with the challenge's {@code serverId} using the session it already holds, and the backend
 * then asks Mojang who joined. The session access token is used exactly once, is passed straight to
 * Mojang and is never logged, stored or sent to FPSMaster.
 *
 * <p>Offline sessions cannot complete this handshake, so they are skipped outright: their loadout
 * still renders locally and still syncs to their FPSMaster account, they are simply not published to
 * other players.
 */
public final class MinecraftLinkService {
    private static final MinecraftLinkService INSTANCE = new MinecraftLinkService();

    public enum State {
        IDLE,
        /** Offline session — no Mojang identity to prove. */
        SKIPPED_OFFLINE,
        LINKING,
        LINKED,
        FAILED
    }

    private static final long RETRY_BASE_MS = 5_000L;
    private static final long RETRY_MAX_MS = 5L * 60_000L;

    private volatile State state = State.IDLE;
    private volatile String message = "";
    /** Session the current state belongs to ({@code uuid:name}); a different session starts over. */
    private volatile String sessionKey;
    private volatile boolean inFlight;
    /** 0 = no further attempt for this session (linked, offline, or rejected for good). */
    private volatile long retryAt;
    private int failures;

    private MinecraftLinkService() {
    }

    public static MinecraftLinkService getInstance() {
        return INSTANCE;
    }

    public State state() {
        return state;
    }

    public String message() {
        return message;
    }

    /**
     * A Mojang session has a version-4 (random) profile UUID and a real access token; an offline
     * launcher produces a version-3 UUID derived from the name, with a placeholder token.
     */
    public static boolean isOnlineSession() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.getSession() == null) {
            return false;
        }
        GameProfile profile = minecraft.getSession().getProfile();
        UUID id = profile == null ? null : profile.getId();
        String token = minecraft.getSession().getToken();
        return id != null && id.version() == 4 && token != null && token.length() > 8;
    }

    /**
     * Runs the handshake for the current Minecraft session. Cheap enough for every tick: it only
     * does work when the session changed (account switch), or when a retryable failure's backoff
     * has elapsed. A linked, offline or permanently rejected session is never retried.
     */
    public synchronized void linkIfNeeded() {
        if (!AuthService.getInstance().isLoggedIn() || inFlight) {
            return;
        }
        String key = currentSessionKey();
        if (key == null) {
            return;
        }
        if (!key.equals(sessionKey)) {
            // Switching Minecraft accounts makes the previous link (or failure) someone else's.
            sessionKey = key;
            state = State.IDLE;
            message = "";
            failures = 0;
            retryAt = System.currentTimeMillis();
        }
        if (retryAt == 0L || System.currentTimeMillis() < retryAt) {
            return;
        }
        if (!isOnlineSession()) {
            state = State.SKIPPED_OFFLINE;
            message = FPSMaster.i18n.get("cosmetics.link.offline");
            retryAt = 0L;
            return;
        }
        inFlight = true;
        state = State.LINKING;
        message = FPSMaster.i18n.get("cosmetics.link.pending");
        final String attemptKey = key;
        FPSMasterApiClient.getInstance().requestMinecraftLinkChallenge().whenComplete((response, error) -> {
            if (error != null || response == null || !response.isSuccess() || response.getData() == null) {
                failBackend(attemptKey, response, error);
                return;
            }
            joinAndConfirm(attemptKey, response.getData());
        });
    }

    /** Forgets this session's outcome so the next {@link #linkIfNeeded()} starts over (sign-in/out). */
    public synchronized void reset() {
        sessionKey = null;
        state = State.IDLE;
        message = "";
        failures = 0;
        retryAt = 0L;
    }

    private void joinAndConfirm(String attemptKey, MinecraftLinkChallenge challenge) {
        if (!challenge.isWellFormed()) {
            fail(attemptKey, false, "malformed challenge");
            return;
        }
        FPSMaster.async.runnable(() -> {
            Minecraft minecraft = Minecraft.getMinecraft();
            GameProfile profile = minecraft.getSession().getProfile();
            try {
                minecraft.getSessionService().joinServer(
                        profile, minecraft.getSession().getToken(), challenge.getServerId());
            } catch (AuthenticationUnavailableException unavailable) {
                // Mojang's session server could not be reached: a network problem, worth retrying.
                fail(attemptKey, true, unavailable.getClass().getSimpleName());
                return;
            } catch (Exception exception) {
                // Invalid or expired session token: retrying the same token cannot succeed. A new
                // session (re-login, account switch) gets a fresh attempt. Deliberately reports the
                // exception type only: the message can echo request details.
                fail(attemptKey, false, exception.getClass().getSimpleName());
                return;
            }
            FPSMasterApiClient.getInstance()
                    .confirmMinecraftLink(challenge.getChallengeId(), profile.getName())
                    .whenComplete((response, error) -> {
                        if (error == null && response != null && response.isSuccess()) {
                            succeed(attemptKey);
                            return;
                        }
                        failBackend(attemptKey, response, error);
                    });
        });
    }

    private synchronized void succeed(String attemptKey) {
        inFlight = false;
        if (!attemptKey.equals(sessionKey)) {
            return;
        }
        state = State.LINKED;
        message = FPSMaster.i18n.get("cosmetics.link.ok");
        failures = 0;
        retryAt = 0L;
    }

    /**
     * Network failures, rate limiting and server errors are transient; any other 4xx is the backend
     * rejecting this request and will not change by asking again.
     */
    private void failBackend(String attemptKey, ApiResponse<?> response, Throwable error) {
        int code = error != null || response == null ? ApiResponse.NETWORK_ERROR : response.getCode();
        boolean retryable = code == ApiResponse.NETWORK_ERROR || code == 429 || code >= 500;
        String reason = error != null ? error.getClass().getSimpleName()
                : response == null ? "no response"
                : "HTTP " + code + (response.getErrorCode() == null ? "" : " " + response.getErrorCode());
        fail(attemptKey, retryable, reason);
    }

    private synchronized void fail(String attemptKey, boolean retryable, String reason) {
        inFlight = false;
        if (!attemptKey.equals(sessionKey)) {
            return;
        }
        state = State.FAILED;
        message = FPSMaster.i18n.get("cosmetics.link.failed");
        if (retryable) {
            failures++;
            long delay = Math.min(RETRY_BASE_MS << Math.min(failures - 1, 20), RETRY_MAX_MS);
            retryAt = System.currentTimeMillis() + delay;
            ClientLogger.warn("Minecraft account link failed: " + reason + ", retrying in " + (delay / 1000L) + "s");
        } else {
            retryAt = 0L;
            ClientLogger.warn("Minecraft account link failed: " + reason);
        }
    }

    private static String currentSessionKey() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.getSession() == null) {
            return null;
        }
        return minecraft.getSession().getPlayerID() + ":" + minecraft.getSession().getUsername();
    }
}
