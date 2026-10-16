package top.fpsmaster.utils.system;

import java.util.List;

/**
 * Process builders for anything the client spawns (explorer, ffmpeg, ...).
 *
 * <p>The launcher hands the platform token over in {@code FPSMASTER_LAUNCH_TOKEN}. Java cannot unset
 * its own environment, but it can keep the variable out of every child: those inherit the whole
 * environment by default and have no business holding the player's token.
 */
public final class ChildProcesses {
    private static final String LAUNCH_TOKEN_ENV = "FPSMASTER_LAUNCH_TOKEN";

    private ChildProcesses() {
    }

    public static ProcessBuilder builder(String... command) {
        return scrub(new ProcessBuilder(command));
    }

    public static ProcessBuilder builder(List<String> command) {
        return scrub(new ProcessBuilder(command));
    }

    private static ProcessBuilder scrub(ProcessBuilder builder) {
        builder.environment().remove(LAUNCH_TOKEN_ENV);
        return builder;
    }
}
