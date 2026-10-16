package top.fpsmaster.ui.screens.music;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.features.impl.interfaces.LyricsDisplay;
import top.fpsmaster.modules.config.ConfigProfileUtils;
import top.fpsmaster.modules.logger.ClientLogger;
import top.fpsmaster.modules.music.MusicManager;
import top.fpsmaster.modules.music.MusicTextures;
import top.fpsmaster.music.Lyric;
import top.fpsmaster.music.LyricLine;
import top.fpsmaster.music.MusicApiException;
import top.fpsmaster.music.MusicErrorKind;
import top.fpsmaster.music.MusicSource;
import top.fpsmaster.music.QrCode;
import top.fpsmaster.music.QrLoginState;
import top.fpsmaster.music.Track;
import top.fpsmaster.prism.screen.MusicBridge;
import top.fpsmaster.prism.screen.SharedMusic;
import top.fpsmaster.prism.widget.Chrome;
import top.fpsmaster.prism.widget.UiFrame;
import top.fpsmaster.ui.kit.EdgeUi;
import top.fpsmaster.utils.io.FileUtils;
import top.fpsmaster.utils.render.draw.Images;
import top.fpsmaster.utils.render.gui.ScaledGuiScreen;
import top.fpsmaster.utils.system.FolderOpen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Edge host for the shared Prism music screen. */
public class MusicScreen extends ScaledGuiScreen {
    private final GuiScreen parent;
    private final MusicManager music = MusicManager.get();
    private final SharedMusic gui = new SharedMusic();
    private final MusicBridge bridge = new EdgeMusicBridge();
    private volatile List<Track> tracks = new ArrayList<Track>();
    private volatile List<Track> localTracks = new ArrayList<Track>();
    private volatile List<MusicManager.PlaylistItem> playlists = new ArrayList<MusicManager.PlaylistItem>();
    private volatile String listTitle = "";
    private volatile String loginStatus = "";
    /*
     * List states for Prism: an empty list alone cannot say "loading", "nothing found", "log in first"
     * or "request failed". The request counters drop answers that arrive after a newer request (search
     * typed while discover was still loading, for example). Written on the client thread only.
     */
    private MusicBridge.ListState tracksState = MusicBridge.ListState.READY;
    private String tracksError = "";
    private int tracksRequest;
    private MusicBridge.ListState playlistsState = MusicBridge.ListState.READY;
    private String playlistsError = "";
    private int playlistsRequest;

    public MusicScreen() {
        this(null);
    }

    public MusicScreen(GuiScreen parent) {
        this.parent = parent;
        music.setVolume(FPSMaster.configManager.configure.musicVolume);
        try {
            music.setPlaybackMode(MusicBridge.PlaybackMode.valueOf(
                    FPSMaster.configManager.configure.musicPlaybackMode));
        } catch (IllegalArgumentException ignored) {
            music.setPlaybackMode(MusicBridge.PlaybackMode.SEQUENTIAL);
        }
        try {
            refreshLocalTracks(FileUtils.musicDirectory());
        } catch (IllegalStateException exception) {
            ClientLogger.warn("Music folder is unavailable: " + exception.getMessage());
        }
    }

    private MusicManager.Cb<List<Track>> tracksCallback() {
        final int request = ++tracksRequest;
        tracksState = MusicBridge.ListState.LOADING;
        tracksError = "";
        return (result, error) -> {
            if (request != tracksRequest) return;
            if (error != null) {
                tracks = new ArrayList<Track>();
                settle(error, true);
                return;
            }
            tracks = result == null ? new ArrayList<Track>() : result;
            tracksState = MusicBridge.ListState.READY;
        };
    }

    private MusicManager.Cb<List<MusicManager.PlaylistItem>> playlistsCallback() {
        final int request = ++playlistsRequest;
        playlistsState = MusicBridge.ListState.LOADING;
        playlistsError = "";
        return (result, error) -> {
            if (request != playlistsRequest) return;
            if (error != null) {
                playlists = new ArrayList<MusicManager.PlaylistItem>();
                settle(error, false);
                return;
            }
            playlists = result == null ? new ArrayList<MusicManager.PlaylistItem>() : result;
            playlistsState = MusicBridge.ListState.READY;
        };
    }

    private void settle(Throwable error, boolean trackList) {
        boolean needLogin = error instanceof MusicApiException
                && ((MusicApiException) error).getKind() == MusicErrorKind.NOT_LOGGED_IN;
        MusicBridge.ListState state = needLogin ? MusicBridge.ListState.NEED_LOGIN : MusicBridge.ListState.ERROR;
        String reason = needLogin ? "" : describe(error);
        if (!needLogin) {
            ClientLogger.warn("Music list request failed: " + error);
        }
        if (trackList) {
            tracksState = state;
            tracksError = reason;
        } else {
            playlistsState = state;
            playlistsError = reason;
        }
    }

    /** User-facing reason for a failed request, by Cadence's error kind rather than its English message. */
    private static String describe(Throwable error) {
        if (!(error instanceof MusicApiException)) {
            return String.format(FPSMaster.i18n.get("music.error.unknown"), error.getClass().getSimpleName());
        }
        MusicApiException api = (MusicApiException) error;
        switch (api.getKind()) {
            case NETWORK:
                return FPSMaster.i18n.get("music.error.network");
            case TIMEOUT:
                return FPSMaster.i18n.get("music.error.timeout");
            case NOT_LOGGED_IN:
                return FPSMaster.i18n.get("music.error.notloggedin");
            case RISK_CONTROL:
                return FPSMaster.i18n.get("music.error.risk");
            case BAD_RESPONSE:
                return FPSMaster.i18n.get("music.error.badresponse");
            default:
                Integer code = api.getPlatformCode();
                return String.format(FPSMaster.i18n.get("music.error.api"), code == null ? "?" : String.valueOf(code));
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        UiFrame ui = EdgeUi.frame();
        if (gui.draw(ui, bridge)) {
            mc.displayGuiScreen(parent);
            return;
        }
        drawOpenFolderButton(ui);
    }

    private void drawOpenFolderButton(UiFrame ui) {
        float gw = ui.host().width();
        float gh = ui.host().height();
        float pw = Math.min(480f, Math.max(320f, gw - 24f));
        float ph = Math.min(290f, Math.max(200f, gh - 32f));
        float px = (gw - pw) / 2f;
        float py = (gh - ph) / 2f;
        float size = 16f;
        float closeX = px + pw - 7f - size;
        float settingsX = closeX - 21f;
        float folderX = settingsX - 21f;
        float folderY = py + 7f;
        String label = FPSMaster.i18n.get("music.local.openfolder");
        float labelW = ui.font(12).measure(label) + 16f;
        float buttonX = folderX - labelW + size;
        if (Chrome.button(ui, buttonX, folderY, labelW, size, label, Chrome.ButtonStyle.GHOST)) {
            openLocalMusicFolder();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (gui.cancelOverlay()) music.stopQrLogin();
            else mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        music.stopQrLogin();
        FPSMaster.configManager.saveConfigQuietly(ConfigProfileUtils.getActiveProfileName());
        super.onGuiClosed();
    }

    private final class EdgeMusicBridge implements MusicBridge {
        @Override public String i18n(String key) { return FPSMaster.i18n.get(key); }
        @Override public boolean qq() { return music.getSource() == MusicSource.QQ; }
        @Override public void setQq(boolean qq) { music.setSource(qq ? MusicSource.QQ : MusicSource.NETEASE); }
        @Override public boolean loggedIn() { return music.isLoggedIn(); }
        @Override public String status() { return music.getStatus(); }
        @Override public String nowTitle() { Track track = music.getCurrent(); return track == null ? i18n("music.lyrics.idle") : track.getName(); }
        @Override public String nowArtist() { Track track = music.getCurrent(); return track == null ? i18n(qq() ? "music.source.qq" : "music.source.netease") : track.getArtists(); }
        @Override public boolean playing() { return music.engine().isPlaying(); }
        @Override public boolean paused() { return music.engine().isPaused(); }
        @Override public long positionMs() { return music.engine().getPositionMs(); }
        @Override public long durationMs() { long duration = music.engine().getDurationMs(); Track track = music.getCurrent(); return duration > 0 || track == null ? duration : track.getDurationMs(); }
        @Override public float progress() { long duration = durationMs(); return duration <= 0 ? 0f : Math.min(1f, positionMs() / (float) duration); }
        @Override public float volume() { return music.getVolume() / 100f; }
        @Override public void setVolume(float value) {
            int volume = Math.round(value * 100f);
            music.setVolume(volume);
            FPSMaster.configManager.configure.musicVolume = volume;
        }
        @Override public void seek(float value) { music.seekFraction(value); }
        @Override public void togglePause() { music.togglePause(); }
        @Override public void next() { music.next(); }
        @Override public void prev() { music.prev(); }
        @Override public PlaybackMode playbackMode() { return music.getPlaybackMode(); }
        @Override public void setPlaybackMode(PlaybackMode mode) {
            music.setPlaybackMode(mode);
            FPSMaster.configManager.configure.musicPlaybackMode = mode.name();
        }

        @Override
        public void play(int index) {
            List<Track> snapshot = tracks;
            if (index >= 0 && index < snapshot.size()) music.playList(snapshot, index);
        }

        @Override
        public List<TrackRow> tracks() {
            List<TrackRow> rows = new ArrayList<TrackRow>();
            for (Track track : tracks) {
                rows.add(new TrackRow(track.getName(), track.getArtists(), format(track.getDurationMs()), track.getVip()));
            }
            return rows;
        }

        @Override public List<TrackRow> localTracks() {
            List<TrackRow> rows = new ArrayList<TrackRow>();
            for (Track track : localTracks) {
                rows.add(new TrackRow(track.getName(), track.getArtists(), format(track.getDurationMs()), false));
            }
            return rows;
        }

        @Override public void importLocalMusic() {
            openLocalMusicFolder();
        }

        @Override public void playLocal(int index) {
            music.playList(localTracks, index);
        }

        @Override public String listTitle() { return listTitle; }
        @Override public ListState tracksState() { return tracksState; }
        @Override public String tracksError() { return tracksError; }
        @Override public ListState playlistsState() { return playlistsState; }
        @Override public String playlistsError() { return playlistsError; }

        @Override public void search(final String query) {
            listTitle = String.format(i18n("music.list.search"), query);
            music.search(query, tracksCallback());
        }

        @Override public void loadDiscover() {
            listTitle = i18n(qq() ? "music.list.toplist" : "music.list.daily");
            music.loadDiscover(tracksCallback());
        }

        @Override public void loadPlaylists() {
            music.loadPlaylists(playlistsCallback());
        }

        @Override public boolean playlists() { return !playlists.isEmpty(); }

        @Override
        public void openPlaylist(int index) {
            List<MusicManager.PlaylistItem> snapshot = playlists;
            if (index < 0 || index >= snapshot.size()) return;
            MusicManager.PlaylistItem playlist = snapshot.get(index);
            listTitle = playlist.name;
            music.loadPlaylistTracks(playlist, tracksCallback());
        }

        @Override
        public List<PlaylistRow> playlistRows() {
            List<PlaylistRow> rows = new ArrayList<PlaylistRow>();
            for (MusicManager.PlaylistItem playlist : playlists) rows.add(new PlaylistRow(playlist.name, String.valueOf(playlist.count)));
            return rows;
        }

        @Override public boolean supportsLogin() { return true; }

        @Override
        public void startLogin() {
            loginStatus = i18n("music.login.status.generating");
            music.startQrLogin(new MusicManager.Cb<QrLoginState>() {
                @Override
                public void done(QrLoginState state, Throwable error) {
                    if (error != null || state == QrLoginState.ERROR) loginStatus = i18n("music.login.status.failed");
                    else if (state == QrLoginState.WAITING) loginStatus = i18n("music.login.status.waiting");
                    else if (state == QrLoginState.SCANNED) loginStatus = i18n("music.login.status.scanned");
                    else if (state == QrLoginState.CONFIRMED) loginStatus = i18n("music.login.status.ok");
                    else if (state == QrLoginState.EXPIRED) loginStatus = i18n("music.login.status.expired");
                }
            });
        }

        @Override public void stopLogin() { music.stopQrLogin(); }
        @Override public void logout() { music.logout(); }
        @Override public String loginStatus() { return loginStatus; }

        @Override
        public void paintLoginQr(UiFrame ui, float x, float y, float size) {
            QrCode qr = music.getQrCode();
            if (qr == null) return;
            String content = qr.getQrContent();
            ResourceLocation texture = content != null && content.startsWith("data:")
                    ? MusicTextures.base64Image(content) : MusicTextures.qr(content);
            if (texture != null) Images.draw(texture, x, y, size, size);
        }

        @Override
        public void submitQqCookie(String musicId, String musicKey) {
            music.qqCookieLogin(musicId, musicKey, (result, error) ->
                    loginStatus = i18n(Boolean.TRUE.equals(result) ? "music.login.status.ok" : "music.login.status.cookiefailed"));
        }

        @Override
        public void paintCover(UiFrame ui, float x, float y, float size) {
            Track track = music.getCurrent();
            if (track == null) return;
            ResourceLocation texture = MusicTextures.cover(track.getCoverUrl());
            if (texture != null) Images.draw(texture, x, y, size, size);
        }

        @Override public boolean hasLyrics() {
            Track track = music.getCurrent();
            return track != null && !track.getId().startsWith("file:");
        }
        @Override public boolean lyricsHudEnabled() { return FPSMaster.moduleManager.getModule(LyricsDisplay.class).isEnabled(); }
        @Override public void setLyricsHudEnabled(boolean enabled) { FPSMaster.moduleManager.getModule(LyricsDisplay.class).set(enabled); }
        @Override public float lyricFontSize() { return LyricsDisplay.fontSize.getValue().floatValue(); }
        @Override public void setLyricFontSize(float size) { LyricsDisplay.fontSize.setValue(size); }
        @Override public int lyricLines() { return LyricsDisplay.lines.getValue().intValue(); }
        @Override public void setLyricLines(int lines) { LyricsDisplay.lines.setValue(lines); }
        @Override public boolean lyricTranslation() { return LyricsDisplay.translation.getValue(); }
        @Override public void setLyricTranslation(boolean enabled) { LyricsDisplay.translation.setValue(enabled); }
        @Override public boolean lyricScroll() { return LyricsDisplay.scroll.getValue(); }
        @Override public void setLyricScroll(boolean enabled) { LyricsDisplay.scroll.setValue(enabled); }
        @Override public boolean lyricBackground() { return LyricsDisplay.background.getValue(); }
        @Override public void setLyricBackground(boolean enabled) { LyricsDisplay.background.setValue(enabled); }
        @Override public int currentLyricIndex() { return music.currentLyricLine(); }

        @Override
        public List<LyricRow> lyricRows() {
            List<LyricRow> rows = new ArrayList<LyricRow>();
            Lyric lyric = music.getCurrentLyric();
            if (lyric == null || lyric.getLines() == null) return rows;
            for (LyricLine line : lyric.getLines()) rows.add(new LyricRow(line.getText(), line.getTranslation()));
            return rows;
        }
    }

    private void openLocalMusicFolder() {
        File folder;
        try {
            folder = FileUtils.musicDirectory();
        } catch (IllegalStateException exception) {
            ClientLogger.warn("Music folder is unavailable: " + exception.getMessage());
            return;
        }
        if (!FolderOpen.open(folder)) {
            ClientLogger.warn("Failed to open local music folder: " + folder.getAbsolutePath());
        }
        refreshLocalTracks(folder);
    }

    /**
     * Scans the music folder off the client thread. One listing serves both the audio files and the
     * cover lookup: the old version listed the folder again for every song (O(n²) on the render
     * thread, a visible hitch with a few hundred files).
     */
    private void refreshLocalTracks(final File folder) {
        FPSMaster.async.runnable(() -> {
            File[] files = folder.listFiles();
            final List<Track> scanned = files == null ? new ArrayList<Track>() : toLocalTracks(files);
            mc.addScheduledTask(() -> localTracks = scanned);
        });
    }

    private static String format(long millis) {
        long seconds = Math.max(0L, millis) / 1000L;
        return String.format("%d:%02d", seconds / 60L, seconds % 60L);
    }

    private static List<Track> toLocalTracks(File[] files) {
        java.util.Map<String, File> byLowerName = new java.util.HashMap<String, File>();
        for (File file : files) {
            if (file.isFile()) byLowerName.put(file.getName().toLowerCase(java.util.Locale.ROOT), file);
        }
        String localArtist = FPSMaster.i18n.get("music.local.title");
        List<Track> result = new ArrayList<Track>();
        for (File file : files) {
            if (!file.isFile() || !isLocalAudio(file.getName())) continue;
            String name = file.getName();
            int dot = name.lastIndexOf('.');
            if (dot > 0) name = name.substring(0, dot);
            File cover = localCover(name.toLowerCase(java.util.Locale.ROOT), byLowerName);
            result.add(new Track(MusicSource.NETEASE, file.toURI().toString(), null, name,
                    localArtist, "", 0L, cover == null ? null : cover.toURI().toString(), false));
        }
        return result;
    }

    private static File localCover(String base, java.util.Map<String, File> byLowerName) {
        String[] names = {base, "cover", "folder"};
        String[] extensions = {"png", "jpg", "jpeg"};
        for (String name : names) {
            for (String extension : extensions) {
                File file = byLowerName.get(name + "." + extension);
                if (file != null) return file;
            }
        }
        return null;
    }

    private static boolean isLocalAudio(String name) {
        String lower = name.toLowerCase(java.util.Locale.ROOT);
        return lower.endsWith(".mp3") || lower.endsWith(".wav")
                || lower.endsWith(".aiff") || lower.endsWith(".aif");
    }
}
