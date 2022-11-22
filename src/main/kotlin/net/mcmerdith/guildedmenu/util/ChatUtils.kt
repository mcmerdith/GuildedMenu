package net.mcmerdith.guildedmenu.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

@Suppress("unused")
object ChatUtils {
    /**
     * Send [message] with [ChatColor.RED] text
     */
    fun CommandSender.sendErrorMessage(message: String) {
        message(ChatColor.RED, message, this)
    }

    /**
     * Send [message] with [ChatColor.YELLOW] text
     */
    fun CommandSender.sendWarningMessage(message: String) {
        message(ChatColor.YELLOW, message, this)
    }

    /**
     * Send [message] with [ChatColor.GREEN] text
     */
    fun CommandSender.sendSuccessMessage(message: String) {
        message(ChatColor.GREEN, message, this)
    }

    /**
     * Send [message] with [ChatColor.AQUA] text
     */
    fun CommandSender.sendInfoMessage(message: String) {
        message(ChatColor.AQUA, message, this)
    }

    /**
     * Send [message] with [color] text to [sender]
     */
    private fun message(color: ChatColor, message: String, sender: CommandSender) {
        sender.sendMessage("$color$message")
    }
}