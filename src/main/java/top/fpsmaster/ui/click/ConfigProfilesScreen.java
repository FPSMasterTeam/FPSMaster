package top.fpsmaster.ui.click;

import org.lwjgl.input.Keyboard;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.exception.FileException;
import top.fpsmaster.features.impl.InterfaceModule;
import top.fpsmaster.features.manager.Module;
import top.fpsmaster.modules.config.ConfigProfileUtils;
import top.fpsmaster.modules.config.ConfigProfileUtils.ConfigProfile;
import top.fpsmaster.modules.logger.ClientLogger;
import top.fpsmaster.prism.overlay.NotificationCenter;
import top.fpsmaster.prism.screen.ConfigProfilesBridge;
import top.fpsmaster.prism.screen.SharedConfigProfiles;
import top.fpsmaster.prism.widget.Chrome;
import top.fpsmaster.prism.widget.TextFit;
import top.fpsmaster.prism.widget.UiFrame;
import top.fpsmaster.ui.kit.EdgeUi;
import top.fpsmaster.ui.notification.NotificationManager;
import top.fpsmaster.utils.render.gui.ScaledGuiScreen;
import top.fpsmaster.utils.system.FileDialogs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Edge host for the shared Prism config-profile screen, plus the Edge-side parts Prism has no slot
 * for: the backup history entry and the "a profile with this name exists" choice on import.
 */
public class ConfigProfilesScreen extends ScaledGuiScreen {
    private final ScaledGuiScreen parent;
    private final SharedConfigProfiles profiles = new SharedConfigProfiles();
    private final ConfigProfilesBridge bridge = new EdgeConfigProfilesBridge();
    /** File waiting for the overwrite / save-as decision; null when no conflict is being asked. */
    private File conflictFile;
    private String conflictName = "";
    private String conflictAlternative = "";

    public ConfigProfilesScreen(ScaledGuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        UiFrame ui = EdgeUi.frame();
        boolean modal = conflictFile != null;
        boolean inputWas = ui.inputEnabled();
        if (modal) {
            ui.setInputEnabled(false);
        }
        try {
            if (profiles.draw(ui, bridge)) {
                mc.displayGuiScreen(parent);
                return;
            }
            drawHistoryButton(ui);
        } finally {
            ui.setInputEnabled(inputWas);
        }
        if (modal) {
            drawConflict(ui);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE && conflictFile != null) {
            conflictFile = null;
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    /** Same panel geometry as SharedConfigProfiles: the entry sits at the right of the left column's header. */
    private void drawHistoryButton(UiFrame ui) {
        float gw = ui.host().width();
        float gh = ui.host().height();
        float pw = Math.min(400f, Math.max(280f, gw - 24f));
        float ph = Math.min(250f, Math.max(180f, gh - 32f));
        float px = (gw - pw) / 2f;
        float py = (gh - ph) / 2f;
        float leftW = 140f;
        String label = i18n("configprofiles.history");
        float w = ui.font(14).measure(label) + 12f;
        if (Chrome.button(ui, px + leftW - 7f - w, py + 7f, w, 14f, label, Chrome.ButtonStyle.GHOST)) {
            mc.displayGuiScreen(new ConfigBackupsScreen(this));
        }
    }

    private void drawConflict(UiFrame ui) {
        float gw = ui.host().width();
        float gh = ui.host().height();
        Chrome.veil(ui, 0.6f);
        float dw = 250f;
        float dh = 84f;
        float dx = (gw - dw) / 2f;
        float dy = (gh - dh) / 2f;
        Chrome.panel(ui, dx, dy, dw, dh, 8f);
        String title = String.format(i18n("configprofiles.import.conflict"), conflictName);
        ui.canvas().drawString(ui.font(13), TextFit.ellipsize(ui.font(13), title, dw - 20f),
                dx + 10f, dy + 10f, ui.theme().textPrimary());
        String desc = String.format(i18n("configprofiles.import.conflict.desc"), conflictAlternative);
        ui.canvas().drawString(ui.font(11), TextFit.ellipsize(ui.font(11), desc, dw - 20f),
                dx + 10f, dy + 26f, ui.theme().textSecondary());
        float bh = 16f;
        float by = dy + dh - 12f - bh;
        float bw = 66f;
        float x = dx + dw - 10f - bw;
        if (Chrome.button(ui, x, by, bw, bh, i18n("configprofiles.import.overwrite"), Chrome.ButtonStyle.DANGER_FILL)) {
            File file = conflictFile;
            conflictFile = null;
            notify(importAs(file, conflictName, true));
            return;
        }
        x -= bw + 6f;
        if (Chrome.button(ui, x, by, bw, bh, i18n("configprofiles.import.saveas"), Chrome.ButtonStyle.PRIMARY)) {
            File file = conflictFile;
            conflictFile = null;
            notify(importAs(file, conflictAlternative, false));
            return;
        }
        x -= bw + 6f;
        if (Chrome.button(ui, x, by, bw, bh, i18n("configprofiles.cancel"), Chrome.ButtonStyle.GHOST)) {
            conflictFile = null;
        }
    }

    /** Runs on the client thread once the import picker returns a file. */
    private void onImportChosen(File file) {
        try {
            String name = ConfigProfileUtils.importTargetName(file);
            if (!ConfigProfileUtils.profileExists(name)) {
                notify(importAs(file, name, false));
                return;
            }
            String alternative = ConfigProfileUtils.nextFreeName(name);
            if (mc.currentScreen != this) {
                // The player left this screen while the picker was open; nobody can answer the
                // question, so take the non-destructive choice.
                notify(importAs(file, alternative, false));
                return;
            }
            conflictFile = file;
            conflictName = name;
            conflictAlternative = alternative;
        } catch (FileException exception) {
            notify(failure("import", "configprofiles.status.import_failed", exception));
        }
    }

    private ConfigProfilesBridge.Result importAs(File file, String name, boolean overwrite) {
        try {
            String imported = ConfigProfileUtils.importProfile(file, name, overwrite);
            ConfigProfileUtils.loadProfile(imported);
            return ConfigProfilesBridge.Result.ok(String.format(i18n("configprofiles.status.imported"), imported));
        } catch (Exception exception) {
            return failure("import", "configprofiles.status.import_failed", exception);
        }
    }

    private void onExportChosen(File chosen) {
        File target = chosen;
        if (!target.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
            target = new File(target.getParentFile(), target.getName() + ".json");
        }
        try {
            ConfigProfileUtils.exportActiveProfile(target);
            notify(ConfigProfilesBridge.Result.ok(String.format(i18n("configprofiles.status.exported"), target.getName())));
        } catch (FileException exception) {
            notify(failure("export", "configprofiles.status.export_failed", exception));
        }
    }

    /** Picker results arrive after the bridge call returned, so they cannot use Prism's status line. */
    private void notify(ConfigProfilesBridge.Result result) {
        NotificationManager.addNotification(i18n("configprofiles.title"), result.message,
                result.ok ? NotificationCenter.Type.SUCCESS : NotificationCenter.Type.ERROR, 4f);
    }

    private static String i18n(String key) {
        return FPSMaster.i18n.get(key);
    }

    private static ConfigProfilesBridge.Result failure(String action, String key, Exception error) {
        ClientLogger.error("Failed to " + action + " config profile: " + error.getMessage());
        return ConfigProfilesBridge.Result.failed(i18n(key));
    }

    private final class EdgeConfigProfilesBridge implements ConfigProfilesBridge {
        @Override public String i18n(String key) { return FPSMaster.i18n.get(key); }
        @Override public String activeName() { return ConfigProfileUtils.getActiveProfileName(); }

        @Override
        public List<Profile> profiles() {
            List<Profile> result = new ArrayList<Profile>();
            for (ConfigProfile profile : ConfigProfileUtils.listConfigs()) {
                File file = profile.getFile();
                result.add(new Profile(profile.getName(), file.lastModified(), file.length()));
            }
            return result;
        }

        @Override
        public int enabledModules() {
            int count = 0;
            for (Module module : FPSMaster.moduleManager.modules) {
                if (module.isEnabled() && !"ClientSettings".equals(module.name)) count++;
            }
            return count;
        }

        @Override
        public int hudModules() {
            int count = 0;
            for (Module module : FPSMaster.moduleManager.modules) {
                if (module instanceof InterfaceModule && module.isEnabled()) count++;
            }
            return count;
        }

        @Override public long activeBytes() { File file = activeFile(); return file == null ? 0L : file.length(); }
        @Override public long activeModified() { File file = activeFile(); return file == null ? 0L : file.lastModified(); }
        @Override public boolean isDefault(String name) { return ConfigProfileUtils.CURRENT_CONFIG.equals(name); }

        @Override
        public Result load(String name) {
            try {
                ConfigProfileUtils.loadProfile(name);
                return Result.ok(String.format(i18n("configprofiles.status.loaded"), name));
            } catch (Exception e) {
                return failure("load", "configprofiles.status.load_failed", e);
            }
        }

        @Override
        public Result delete(String name) {
            try {
                ConfigProfileUtils.deleteProfile(name);
                if (name.equals(activeName())) ConfigProfileUtils.loadProfileWithoutSavingCurrent(ConfigProfileUtils.CURRENT_CONFIG);
                return Result.ok(String.format(i18n("configprofiles.status.deleted"), name));
            } catch (Exception e) {
                return failure("delete", "configprofiles.status.delete_failed", e);
            }
        }

        @Override
        public Result rename(String from, String to) {
            try {
                String renamed = ConfigProfileUtils.renameProfile(from, to, "");
                if (from.equals(activeName())) ConfigProfileUtils.setActiveProfileName(renamed);
                return Result.ok(String.format(i18n("configprofiles.status.renamed"), renamed));
            } catch (FileException e) {
                return failure("rename", "configprofiles.status.rename_failed", e);
            }
        }

        @Override
        public Result create(String name) {
            try {
                String created = ConfigProfileUtils.saveCurrentAs(name);
                return Result.ok(String.format(i18n("configprofiles.status.renamed"), created));
            } catch (FileException e) {
                return failure("create", "configprofiles.status.rename_failed", e);
            }
        }

        @Override
        public Result exportActive() {
            boolean opened = FileDialogs.open(i18n("configprofiles.filedialog.export"), true,
                    ConfigProfileUtils.getProfileDir(), activeName() + ".json", null,
                    ConfigProfilesScreen.this::onExportChosen);
            // The outcome arrives later as a notification; nothing to say on the status line yet.
            return opened ? Result.ok("") : Result.failed(i18n("configprofiles.filedialog.busy"));
        }

        @Override
        public Result importFile() {
            boolean opened = FileDialogs.open(i18n("configprofiles.filedialog.import"), false,
                    ConfigProfileUtils.getProfileDir(), null,
                    (dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".json"),
                    ConfigProfilesScreen.this::onImportChosen);
            return opened ? Result.ok("") : Result.failed(i18n("configprofiles.filedialog.busy"));
        }

        @Override
        public Result resetAllOff() {
            try {
                // Resetting is destructive; keep what it replaces so it can be rolled back.
                ConfigProfileUtils.snapshotProfile(activeName(), true);
                ConfigProfileUtils.resetActiveProfileToDefaults();
                return Result.ok(String.format(i18n("configprofiles.status.alloff"), activeName()));
            } catch (FileException e) {
                return failure("reset", "configprofiles.status.alloff_failed", e);
            }
        }

        private File activeFile() {
            try {
                return ConfigProfileUtils.getProfileFile(activeName());
            } catch (FileException e) {
                return null;
            }
        }
    }
}
