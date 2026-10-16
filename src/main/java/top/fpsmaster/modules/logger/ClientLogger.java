package top.fpsmaster.modules.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Every line goes through {@link Redactor}: logs are what players paste into bug reports. */
public class ClientLogger {
    private static final Logger logger = LogManager.getLogger("FPSMaster");

    public static void info(String s) {
        logger.info(Redactor.redact(s));
    }

    public static void error(String s) {
        logger.error(Redactor.redact(s));
    }

    public static void warn(String s) {
        logger.warn(Redactor.redact(s));
    }

    public static void debug(String s) {
        logger.debug(Redactor.redact(s));
    }

    public static void fatal(String s) {
        logger.fatal(Redactor.redact(s));
    }

    public static void trace(String s) {
        logger.trace(Redactor.redact(s));
    }

    public static void info(String from, String s) {
        logger.info("{} -> {}", from, Redactor.redact(s));
    }

    public static void error(String from, String s) {
        logger.error("{} -> {}", from, Redactor.redact(s));
    }

    // Throwable overloads: without these, callers that catch an exception have no way to record it
    // and the stack trace is silently dropped. The trace is rendered and redacted here because an
    // exception message can carry a URL or header with a token in it.
    public static void error(String s, Throwable throwable) {
        logger.error(Redactor.redact(s) + System.lineSeparator() + Redactor.redact(throwable));
    }

    public static void warn(String s, Throwable throwable) {
        logger.warn(Redactor.redact(s) + System.lineSeparator() + Redactor.redact(throwable));
    }
}
