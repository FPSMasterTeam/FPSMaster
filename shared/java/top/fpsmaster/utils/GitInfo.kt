package top.fpsmaster.utils

import top.fpsmaster.modules.logger.Logger.error
import java.io.IOException
import java.util.*

object GitInfo {
    private val properties = Properties()

    init {
        val inputStream = GitInfo::class.java.getResourceAsStream("/git.properties")
        try {
            properties.load(inputStream)
        } catch (e: IOException) {
            error("Failed to load git.properties file")
        }
    }

    val commitId: String
        get() = properties.getProperty("git.commit.id")

    val commitIdAbbrev: String
        get() = properties.getProperty("git.commit.id.abbrev")

    val commitTime: String
        get() = properties.getProperty("git.commit.time")

    val branch: String
        get() = properties.getProperty("git.branch")
}