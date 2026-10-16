package top.fpsmaster.ui.click;

import org.lwjgl.input.Keyboard;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.modules.config.ConfigBackups;
import top.fpsmaster.modules.config.ConfigProfileUtils;
import top.fpsmaster.modules.logger.ClientLogger;
import top.fpsmaster.prism.overlay.NotificationCenter;
import top.fpsmaster.prism.widget.Chrome;
import top.fpsmaster.prism.widget.Scroll;
import top.fpsmaster.prism.widget.TextFit;
import top.fpsmaster.prism.widget.UiFrame;
import top.fpsmaster.ui.kit.EdgeUi;
import top.fpsmaster.ui.notification.NotificationManager;
import top.fpsmaster.utils.render.gui.ScaledGuiScreen;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Backup history of the active config profile, with one-click rollback (confirmed first). */
public class ConfigBackupsScreen extends ScaledGuiScreen {
    private static final float ROW_H = 24f;

    private final ScaledGuiScreen parent;
    private final Scroll scroll = new Scroll("config.backups");
    private final String profile = ConfigProfileUtils.getActiveProfileName();
    private List<ConfigBackups.Backup> backups;
    /** Backup waiting for confirmation; null when no confirmation is shown. */
    private ConfigBackups.Backup confirming;

    public ConfigBackupsScreen(ScaledGuiScreen parent) {
        this.parent = parent;
        reload();
    }

    private void reload() {
        backups = ConfigBackups.forProfiles().list(profile);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        UiFrame ui = EdgeUi.frame();
        float gw = ui.host().width();
        float gh = ui.host().height();
        Chrome.veil(ui, 1f);
        float pw = Math.min(360f, Math.max(260f, gw - 24f));
        float ph = Math.min(250f, Math.max(180f, gh - 32f));
        float px = (gw - pw) / 2f;
        float py = (gh - ph) / 2f;
        Chrome.panel(ui, px, py, pw, ph);

        boolean modal = confirming != null;
        boolean inputWas = ui.inputEnabled();
        if (modal) {
            ui.setInputEnabled(false);
        }
        try {
            if (Chrome.button(ui, px + 7f, py + 7f, 40f, 14f, i18n("configprofiles.back"), Chrome.ButtonStyle.GHOST)) {
                mc.displayGuiScreen(parent);
                return;
            }
            ui.canvas().drawString(ui.font(16), i18n("configprofiles.history.title"),
                    px + 54f, py + 8f, ui.theme().textPrimary());
            String subtitle = String.format(i18n("configprofiles.history.subtitle"), profile,
                    Integer.valueOf(ConfigBackups.KEEP));
            ui.canvas().drawString(ui.font(11), TextFit.ellipsize(ui.font(11), subtitle, pw - 24f),
                    px + 12f, py + 28f, ui.theme().textSecondary());
            Chrome.hairlineH(ui, px + 1f, py + 42f, pw - 2f);
            drawList(ui, px + 10f, py + 48f, pw - 20f, ph - 58f);
        } finally {
            ui.setInputEnabled(inputWas);
        }
        if (modal) {
            drawConfirm(ui, px, py, pw, ph);
        }
    }

    private void drawList(UiFrame ui, float x, float y, float w, float h) {
        if (backups.isEmpty()) {
            String empty = i18n("configprofiles.history.empty");
            ui.canvas().drawString(ui.font(12), TextFit.ellipsize(ui.font(12), empty, w),
                    x + 2f, y + 6f, ui.theme().textDisabled());
            return;
        }
        float off = scroll.begin(ui, x, y, w, h, backups.size() * ROW_H);
        for (int i = 0; i < backups.size(); i++) {
            ConfigBackups.Backup backup = backups.get(i);
            float ry = y + i * ROW_H + off;
            if (ry + ROW_H < y || ry > y + h) {
                continue;
            }
            Chrome.card(ui, x, ry + 1f, w - 6f, ROW_H - 3f, ui.hovered(x, ry + 1f, w - 6f, ROW_H - 3f), false);
            ui.canvas().drawString(ui.font(13), formatTime(backup.getCreatedAt()),
                    x + 8f, ry + 6f, ui.theme().textPrimary());
            String size = formatSize(backup.getFile().length());
            ui.canvas().drawString(ui.font(11), size, x + 8f + ui.font(13).measure("0000-00-00 00:00:00") + 10f,
                    ry + 7f, ui.theme().textDisabled());
            String label = i18n("configprofiles.history.restore");
            float bw = ui.font(14).measure(label) + 16f;
            if (Chrome.button(ui, x + w - 12f - bw, ry + 4f, bw, 14f, label, Chrome.ButtonStyle.DEFAULT)) {
                confirming = backup;
            }
        }
        scroll.end(ui);
    }

    private void drawConfirm(UiFrame ui, float px, float py, float pw, float ph) {
        ui.canvas().fillRect(px, py, pw, ph, 0x99000000);
        float dw = 240f;
        float dh = 72f;
        float dx = px + (pw - dw) / 2f;
        float dy = py + (ph - dh) / 2f;
        Chrome.panel(ui, dx, dy, dw, dh, 8f);
        String title = String.format(i18n("configprofiles.history.confirm"), formatTime(confirming.getCreatedAt()));
        ui.canvas().drawString(ui.font(12), TextFit.ellipsize(ui.font(12), title, dw - 20f),
                dx + 10f, dy + 12f, ui.theme().textPrimary());
        float bw = 56f;
        float bh = 16f;
        float by = dy + dh - 12f - bh;
        if (Chrome.button(ui, dx + dw - 10f - bw, by, bw, bh, i18n("configprofiles.history.restore"),
                Chrome.ButtonStyle.PRIMARY)) {
            restore(confirming);
            confirming = null;
            return;
        }
        if (Chrome.button(ui, dx + dw - 16f - bw * 2f, by, bw, bh, i18n("configprofiles.cancel"),
                Chrome.ButtonStyle.GHOST)) {
            confirming = null;
        }
    }

    private void restore(ConfigBackups.Backup backup) {
        try {
            ConfigProfileUtils.restoreBackup(profile, backup);
            NotificationManager.addNotification(i18n("configprofiles.history.title"),
                    String.format(i18n("configprofiles.history.restored"), formatTime(backup.getCreatedAt())),
                    NotificationCenter.Type.SUCCESS, 4f);
        } catch (Exception exception) {
            ClientLogger.error("Failed to restore config backup " + backup.getFile().getName() + ": " + exception);
            NotificationManager.addNotification(i18n("configprofiles.history.title"),
                    i18n("configprofiles.history.restore_failed"), NotificationCenter.Type.ERROR, 4f);
        }
        reload();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (confirming != null) {
                confirming = null;
            } else {
                mc.displayGuiScreen(parent);
            }
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    private static String i18n(String key) {
        return FPSMaster.i18n.get(key);
    }

    private static String formatTime(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(millis));
    }

    private static String formatSize(long bytes) {
        return bytes < 1024L ? bytes + " B" : (bytes / 1024L) + " KB";
    }
}
