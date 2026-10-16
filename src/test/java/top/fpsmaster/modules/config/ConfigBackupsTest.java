package top.fpsmaster.modules.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import top.fpsmaster.utils.io.AtomicFiles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigBackupsTest {
    @TempDir
    Path temp;

    private File write(String content) throws IOException {
        File file = temp.resolve("default.json").toFile();
        AtomicFiles.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    @Test
    void regularSavesSnapshotAtMostEveryInterval() throws IOException {
        ConfigBackups backups = new ConfigBackups(temp.resolve("backups").toFile());
        long now = 1_700_000_000_000L;
        File config = write("{\"v\":1}");
        assertNotNull(backups.snapshot("default", config, false, now));

        write("{\"v\":2}");
        assertNull(backups.snapshot("default", config, false, now + 1_000L), "inside the interval");
        assertNotNull(backups.snapshot("default", config, false, now + ConfigBackups.MIN_INTERVAL_MS));
        // Same content as the newest backup is never stored twice, even when forced.
        assertNull(backups.snapshot("default", config, true, now + 3 * ConfigBackups.MIN_INTERVAL_MS));

        write("{\"v\":3}");
        assertNotNull(backups.snapshot("default", config, true, now + ConfigBackups.MIN_INTERVAL_MS + 5L),
                "forced snapshots ignore the interval");
        assertEquals(3, backups.list("default").size());
    }

    @Test
    void corruptFilesAreNeverBackedUpAndRecoveryPicksNewestValid() throws IOException {
        ConfigBackups backups = new ConfigBackups(temp.resolve("backups").toFile());
        long now = 1_700_000_000_000L;
        File config = write("{\"good\":true}");
        backups.snapshot("default", config, true, now);

        write("{\"half\": ");
        assertNull(backups.snapshot("default", config, true, now + 10L));
        write("");
        assertNull(backups.snapshot("default", config, true, now + 20L));

        ConfigBackups.Backup newest = backups.newestValid("default");
        assertNotNull(newest);
        assertArrayEquals("{\"good\":true}".getBytes(StandardCharsets.UTF_8),
                Files.readAllBytes(newest.getFile().toPath()));
    }

    @Test
    void keepsOnlyTheNewestBackupsNewestFirst() throws IOException {
        ConfigBackups backups = new ConfigBackups(temp.resolve("backups").toFile());
        long now = 1_700_000_000_000L;
        File config = temp.resolve("default.json").toFile();
        for (int i = 0; i < ConfigBackups.KEEP + 5; i++) {
            write("{\"v\":" + i + "}");
            assertNotNull(backups.snapshot("default", config, true, now + i * 1_000L));
        }
        List<ConfigBackups.Backup> list = backups.list("default");
        assertEquals(ConfigBackups.KEEP, list.size());
        assertEquals(now + (ConfigBackups.KEEP + 4) * 1_000L, list.get(0).getCreatedAt());
        for (int i = 1; i < list.size(); i++) {
            assertTrue(list.get(i - 1).getCreatedAt() > list.get(i).getCreatedAt());
        }
        assertArrayEquals(("{\"v\":" + (ConfigBackups.KEEP + 4) + "}").getBytes(StandardCharsets.UTF_8),
                Files.readAllBytes(list.get(0).getFile().toPath()));
    }

    @Test
    void atomicWriteReplacesWithoutLeavingTempFiles() throws IOException {
        File config = write("{\"v\":1}");
        AtomicFiles.write(config.toPath(), "{\"v\":2}".getBytes(StandardCharsets.UTF_8));
        assertArrayEquals("{\"v\":2}".getBytes(StandardCharsets.UTF_8), Files.readAllBytes(config.toPath()));
        String[] names = temp.toFile().list();
        assertNotNull(names);
        for (String name : names) {
            assertTrue(!name.endsWith(".tmp"), "leftover temp file " + name);
        }
    }
}
