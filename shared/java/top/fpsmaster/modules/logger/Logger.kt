package top.fpsmaster.modules.logger

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Logger {
    var logger: Logger = LogManager.getLogger("FPSMaster")
    @JvmStatic
    fun info(s: String?) {
        logger.info(s)
    }

    @JvmStatic
    fun error(s: String?) {
        logger.error(s)
    }

    @JvmStatic
    fun warn(s: String?) {
        logger.warn(s)
    }

    fun debug(s: String?) {
        logger.debug(s)
    }

    fun fatal(s: String?) {
        logger.fatal(s)
    }

    fun trace(s: String?) {
        logger.trace(s)
    }

    @JvmStatic
    fun info(from: String, s: String) {
        logger.info("$from -> $s")
    }

    @JvmStatic
    fun error(from: String, s: String) {
        logger.error("$from -> $s")
    }
}
