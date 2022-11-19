package net.mcmerdith.guildedmenu.util

import net.mcmerdith.guildedmenu.GuildedMenu
import java.util.logging.Level
import java.util.logging.Logger

class GMLogger private constructor(name: String = "") {
    val name: String

    init {
        this.name = if (name.isBlank()) "" else "[$name] "
    }

    companion object {
        private lateinit var logger: Logger

        private val loggers = mutableMapOf<String, GMLogger>()

        val MAIN = getLogger("")
        val CONFIG = getLogger("Config")
        val EVENT = getLogger("Event")

        fun init(logger: Logger) {
            this.logger = logger
        }

        fun getLogger(name: String): GMLogger {
            if (!loggers.containsKey(name)) loggers[name] = GMLogger(name)

            return loggers[name]!!
        }
    }

    fun debug(message: String) {
        if (GuildedMenu.plugin.config.debug) info(message)
    }

    fun info(message: String) {
        log(Level.INFO, message)
    }

    fun warn(message: String) {
        log(Level.WARNING, message)
    }

    fun error(message: String, exception: Exception) {
        log(Level.WARNING, message)
        logExceptions(Level.WARNING, exception)
    }

    fun exception(message: String, exception: Throwable) {
        log(Level.SEVERE, message)
        logExceptions(Level.SEVERE, exception)
    }

    fun logExceptions(level: Level, exception: Throwable) {
        var current: Throwable? = exception

        while (current != null) {
            log(level, current.message ?: "Unknown")
            current = exception.cause
        }
    }

    fun log(level: Level, message: String) {
        logger.log(level, "$name$message")
    }
}