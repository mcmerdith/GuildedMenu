package net.mcmerdith.guildedmenu.components

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.ChatUtils.sendSuccessMessage
import net.mcmerdith.guildedmenu.util.Globals
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import javax.annotation.Nonnull

object BusinessManager : CommandExecutor {
    override fun onCommand(
        @Nonnull commandSender: CommandSender,
        @Nonnull command: Command,
        @Nonnull s: String,
        @Nonnull strings: Array<String>
    ): Boolean {
        if (commandSender !is Player) {
            commandSender.sendErrorMessage("Only players can use business commands")
            return false
        }
        if (strings.isEmpty()) return false
        when (strings[0]) {
            "register" -> {
                val name = StringBuilder()
                var i = 0
                while (i < strings.size) {
                    name.append(strings[i])
                    if (i != strings.size - 1) name.append(" ")
                    i++
                }
                val success = register(commandSender.uniqueId, name.toString())
                if (success) {
                    commandSender.sendSuccessMessage("Successfully registered '$name'")
                } else {
                    commandSender.sendErrorMessage("Failed to register '$name'")
                }
            }

            "delete" -> {
            }
        }
        return true
    }

    // Data Storage
    fun init() {
        val names = Globals.allBusinessIds
        for (name in names) {
            val b = Business.load(name)

            if (b == null) {
                GuildedMenu.plugin.logger.warning("Could not load business '$name'")
                continue
            }

            businessMap[b.owner!!] = b
        }
    }

    private val businessMap: MutableMap<UUID, Business> = HashMap()

    fun register(entity: UUID, name: String): Boolean {
        val b: Business = Business.create(name, entity, null) ?: return false
        businessMap[entity] = b
        return true
    }
}