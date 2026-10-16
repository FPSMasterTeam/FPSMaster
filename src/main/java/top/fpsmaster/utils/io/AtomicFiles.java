package top.fpsmaster.utils.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * Whole-file replacement that never leaves a half-written target behind.
 *
 * <p>Truncating the target and then writing it means a crash, power loss or a killed process in
 * between leaves an empty or half JSON file, which used to cost players their whole config. Here the
 * bytes go to a uniquely named sibling temp file first (unique so two clients sharing a directory
 * never write the same temp), are forced to disk, and only then replace the target in one rename.
 */
public final class AtomicFiles {
    private AtomicFiles() {
    }

    public static void write(Path target, byte[] content) throws IOException {
        Path parent = target.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        Path temp = Files.createTempFile(parent, target.getFileName().toString() + ".", ".tmp");
        try {
            try (FileChannel channel = FileChannel.open(temp, StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.wrap(content);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.force(true);
            }
            try {
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unsupported) {
                // Some network / FAT volumes cannot rename atomically; a plain replace is still far
                // better than truncating in place, since the complete file already exists.
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException failure) {
            try {
                Files.deleteIfExists(temp);
            } catch (IOException suppressed) {
                failure.addSuppressed(suppressed);
            }
            throw failure;
        }
    }
}
