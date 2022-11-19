package net.mcmerdith.guildedmenu.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

@Suppress("unused")
object ChatUtils {
    fun CommandSender.sendErrorMessage(message: String) {
        message(ChatColor.RED, message, this)
    }

    fun CommandSender.sendWarningMessage(message: String) {
        message(ChatColor.YELLOW, message, this)
    }

    fun CommandSender.sendSuccessMessage(message: String) {
        message(ChatColor.GREEN, message, this)
    }

    fun CommandSender.sendInfoMessage(message: String) {
        message(ChatColor.AQUA, message, this)
    }

    fun message(color: ChatColor, message: String, sender: CommandSender) {
        sender.sendMessage("$color$message")
    }
}