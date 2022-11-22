package net.mcmerdith.guildedmenu.util

import net.mcmerdith.guildedmenu.GuildedMenu
import java.util.logging.Level
import java.util.logging.Logger

@Suppress("unused")
class GMLogger private constructor(name: String = "") {
    val name: String

    init {
        this.name = if (name.isBlank()) "" else "[$name] "
    }

    companion object {
        private lateinit var logger: Logger

        private val loggers = mutableMapOf<String, GMLogger>()

        val MAIN = getLogger("")
        val FILE = getLogger("File Manager")

        /**
         * Set the [logger] that all [GMLogger]s will use
         */
        fun init(logger: Logger) {
            this.logger = logger
        }

        /**
         * Get a [GMLogger] with [name]
         */
        fun getLogger(name: String): GMLogger {
            if (!loggers.containsKey(name)) loggers[name] = GMLogger(name)

            return loggers[name]!!
        }
    }

    /**
     * [info] ([message]) if debug is set
     */
    fun debug(message: String) {
        if (GuildedMenu.plugin.config.debug) info(message)
    }

    /**
     * Send [message] at [Level.INFO]
     */
    fun info(message: String) {
        log(Level.INFO, message)
    }

    /**
     * Send [message] at [Level.WARNING]
     */
    fun warn(message: String) {
        log(Level.WARNING, message)
    }

    /**
     * Send [message] at [Level.WARNING]
     *
     * Additionally, logs the cause of [exception]
     */
    fun error(message: String, exception: Exception) {
        log(Level.WARNING, message)
        logExceptions(Level.WARNING, exception)
    }

    /**
     * Send [message] at [Level.SEVERE]
     *
     * Additionally, logs the cause of [exception]
     */
    fun exception(message: String, exception: Throwable) {
        log(Level.SEVERE, message)
        logExceptions(Level.SEVERE, exception)
    }

    /**
     * Log the cause of [exception] at [level]
     */
    private fun logExceptions(level: Level, exception: Throwable) {
        var current: Throwable? = exception

        while (current != null) {
            log(level, current.message ?: "Unknown")
            current = exception.cause
        }
    }

    /**
     * Send [message] at [level]
     */
    fun log(level: Level, message: String) {
        logger.log(level, "$name$message")
    }
}