package top.fpsmaster.utils.system;

import net.minecraft.client.Minecraft;
import top.fpsmaster.modules.logger.ClientLogger;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Native file pickers that do not freeze the game.
 *
 * <p>An AWT {@link FileDialog} is modal: {@code setVisible(true)} blocks its caller until the dialog
 * closes. Called from a GUI callback that is the render thread, so the game stopped rendering and
 * ticking (Windows showed "not responding", a server kicked the player on keepalive timeout after
 * ~30 s). The dialog now blocks a daemon thread instead and the choice is handed back on the client
 * thread via {@link Minecraft#addScheduledTask}.
 */
public final class FileDialogs {
    /** One picker at a time: a second click while one is open would stack modal dialogs. */
    private static final AtomicBoolean OPEN = new AtomicBoolean(false);

    private FileDialogs() {
    }

    /**
     * @param onChosen runs on the client thread with the chosen file, or is not called on cancel
     * @return false when another picker is already open
     */
    public static boolean open(String title, boolean save, File directory, String fileName,
                               FilenameFilter filter, Consumer<File> onChosen) {
        if (!OPEN.compareAndSet(false, true)) {
            return false;
        }
        Thread thread = new Thread(() -> {
            File chosen = null;
            try {
                FileDialog dialog = new FileDialog((Frame) null, title, save ? FileDialog.SAVE : FileDialog.LOAD);
                if (directory != null) {
                    dialog.setDirectory(directory.getAbsolutePath());
                }
                if (fileName != null) {
                    dialog.setFile(fileName);
                }
                if (filter != null) {
                    dialog.setFilenameFilter(filter);
                }
                dialog.setVisible(true);
                if (dialog.getFile() != null) {
                    chosen = new File(dialog.getDirectory(), dialog.getFile());
                }
                dialog.dispose();
            } catch (RuntimeException | LinkageError exception) {
                // Headless JREs and broken AWT installs throw here; the caller simply gets no file.
                ClientLogger.error("File dialog failed: " + exception);
            } finally {
                OPEN.set(false);
            }
            if (chosen != null) {
                File result = chosen;
                Minecraft.getMinecraft().addScheduledTask(() -> onChosen.accept(result));
            }
        }, "FPSMaster-FileDialog");
        thread.setDaemon(true);
        thread.start();
        return true;
    }
}
