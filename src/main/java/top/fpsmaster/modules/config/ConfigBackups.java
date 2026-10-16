package top.fpsmaster.modules.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import top.fpsmaster.modules.logger.ClientLogger;
import top.fpsmaster.utils.io.AtomicFiles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Rolling, timestamped copies of each config profile under {@code config/backups/<profile>/}.
 *
 * <p>Saves are frequent (every settings change autosaves), so a backup per save would leave the last
 * {@link #KEEP} slider steps and nothing older. Instead a save first snapshots the file it is about to
 * replace only when the newest backup is at least {@link #MIN_INTERVAL_MS} old and differs from it;
 * destructive operations (import over a profile, reset, restore) snapshot unconditionally. Only files
 * that parse as a JSON object are ever kept, so a corrupt file can never push out the good backups
 * that a recovery needs.
 */
public final class ConfigBackups {
    public static final int KEEP = 10;
    public static final long MIN_INTERVAL_MS = 5L * 60_000L;
    private static final String SUFFIX = ".json";
    private static final String PATTERN = "yyyyMMdd-HHmmss-SSS";

    private final File root;

    public ConfigBackups(File root) {
        this.root = root;
    }

    /** The backups of the regular profile directory. */
    public static ConfigBackups forProfiles() {
        return new ConfigBackups(new File(ConfigProfileUtils.getProfileDir(), "backups"));
    }

    public static final class Backup {
        private final File file;
        private final long createdAt;

        private Backup(File file, long createdAt) {
            this.file = file;
            this.createdAt = createdAt;
        }

        public File getFile() {
            return file;
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }

    /** Newest first. */
    public List<Backup> list(String profile) {
        File[] files = dir(profile).listFiles((d, name) -> name.endsWith(SUFFIX));
        if (files == null) {
            return Collections.emptyList();
        }
        List<Backup> backups = new ArrayList<>();
        for (File file : files) {
            long created = parseTimestamp(file.getName());
            if (created > 0L) {
                backups.add(new Backup(file, created));
            }
        }
        backups.sort(Comparator.comparingLong(Backup::getCreatedAt).reversed());
        return backups;
    }

    /**
     * Copies {@code current} into the profile's backups if it is worth keeping.
     *
     * @param force skip the interval check (but never the validity / duplicate checks)
     * @return the new backup, or null when nothing was written
     */
    public Backup snapshot(String profile, File current, boolean force, long now) throws IOException {
        if (current == null || !current.isFile() || current.length() == 0L) {
            return null;
        }
        byte[] content = Files.readAllBytes(current.toPath());
        if (!isJsonObject(content)) {
            return null;
        }
        List<Backup> existing = list(profile);
        if (!existing.isEmpty()) {
            Backup newest = existing.get(0);
            if (!force && now - newest.createdAt < MIN_INTERVAL_MS) {
                return null;
            }
            if (Arrays.equals(content, Files.readAllBytes(newest.file.toPath()))) {
                return null;
            }
        }
        File target = uniqueFile(profile, now);
        AtomicFiles.write(target.toPath(), content);
        prune(profile);
        return new Backup(target, now);
    }

    /** The newest backup whose content is a JSON object, or null. */
    public Backup newestValid(String profile) {
        for (Backup backup : list(profile)) {
            try {
                if (isJsonObject(Files.readAllBytes(backup.file.toPath()))) {
                    return backup;
                }
            } catch (IOException exception) {
                ClientLogger.warn("Unreadable config backup " + backup.file.getName() + ": " + exception.getMessage());
            }
        }
        return null;
    }

    /** Moves a profile's backups along with a rename. */
    public void rename(String from, String to) {
        File source = dir(from);
        if (!source.isDirectory()) {
            return;
        }
        File target = dir(to);
        if (target.exists() || !source.renameTo(target)) {
            ClientLogger.warn("Config backups of " + from + " were not moved to " + to);
        }
    }

    static boolean isJsonObject(byte[] content) {
        try {
            JsonElement element = new JsonParser().parse(new String(content, StandardCharsets.UTF_8));
            return element != null && element.isJsonObject();
        } catch (RuntimeException invalid) {
            return false;
        }
    }

    private void prune(String profile) {
        List<Backup> backups = list(profile);
        for (int i = KEEP; i < backups.size(); i++) {
            File file = backups.get(i).file;
            if (!file.delete()) {
                ClientLogger.warn("Failed to prune config backup: " + file.getAbsolutePath());
            }
        }
    }

    private File dir(String profile) {
        return new File(root, profile);
    }

    private File uniqueFile(String profile, long now) {
        String stamp = new SimpleDateFormat(PATTERN, Locale.ROOT).format(new Date(now));
        File file = new File(dir(profile), stamp + SUFFIX);
        // Two snapshots in the same millisecond (a forced one right after a regular one): step forward
        // so neither overwrites the other and the order stays right.
        while (file.exists()) {
            now++;
            stamp = new SimpleDateFormat(PATTERN, Locale.ROOT).format(new Date(now));
            file = new File(dir(profile), stamp + SUFFIX);
        }
        return file;
    }

    private static long parseTimestamp(String fileName) {
        String stamp = fileName.substring(0, fileName.length() - SUFFIX.length());
        try {
            SimpleDateFormat format = new SimpleDateFormat(PATTERN, Locale.ROOT);
            format.setLenient(false);
            return format.parse(stamp).getTime();
        } catch (ParseException notOurs) {
            return 0L;
        }
    }
}
