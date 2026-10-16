package top.fpsmaster.modules.logger;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scrubs credentials from text before it reaches a log line or a report.
 *
 * <p>Two layers: exact values registered at runtime ({@link #registerSecret}, e.g. the launch token
 * once it has been read), and patterns for credential shapes we did not see coming — JWTs, bearer
 * headers, {@code -Dfpsmaster.auth.token=...} and {@code key: value} pairs named like tokens.
 */
public final class Redactor {
    public static final String MASK = "<redacted>";
    /** Shorter values are not worth masking and would mangle ordinary words. */
    private static final int MIN_SECRET_LENGTH = 8;

    private static final Set<String> SECRETS = new CopyOnWriteArraySet<>();

    private static final Pattern[] PATTERNS = {
            // JSON Web Tokens: three base64url segments, the first one always starts with eyJ.
            Pattern.compile("eyJ[A-Za-z0-9_-]{4,}\\.[A-Za-z0-9_-]{4,}\\.[A-Za-z0-9_-]*"),
            Pattern.compile("(?i)(bearer\\s+)[A-Za-z0-9._~+/=-]{8,}"),
            Pattern.compile("(?i)(-Dfpsmaster\\.auth\\.[A-Za-z]+=)\\S+"),
            Pattern.compile("(?i)(FPSMASTER_LAUNCH_TOKEN=)\\S+"),
            Pattern.compile("(?i)((?:access|refresh|auth|launch)[_-]?token\"?\\s*[:=]\\s*\"?)[^\"\\s,;}&]+"),
            Pattern.compile("(?i)((?:password|passwd|cookie|MUSIC_U|qm_keyst)\"?\\s*[:=]\\s*\"?)[^\"\\s,;}&]+"),
    };

    private Redactor() {
    }

    public static void registerSecret(String secret) {
        if (secret != null && secret.length() >= MIN_SECRET_LENGTH) {
            SECRETS.add(secret);
        }
    }

    public static String redact(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String result = text;
        for (String secret : SECRETS) {
            if (result.contains(secret)) {
                result = result.replace(secret, MASK);
            }
        }
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(result);
            if (!matcher.find()) {
                continue;
            }
            StringBuffer out = new StringBuffer();
            do {
                String prefix = matcher.groupCount() >= 1 && matcher.group(1) != null ? matcher.group(1) : "";
                matcher.appendReplacement(out, Matcher.quoteReplacement(prefix + MASK));
            } while (matcher.find());
            matcher.appendTail(out);
            result = out.toString();
        }
        return result;
    }

    /** The full stack trace of {@code throwable}, redacted. */
    public static String redact(Throwable throwable) {
        java.io.StringWriter writer = new java.io.StringWriter();
        throwable.printStackTrace(new java.io.PrintWriter(writer));
        return redact(writer.toString());
    }
}
