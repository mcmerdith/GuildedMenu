package net.mcmerdith.guildedmenu.business

import net.mcmerdith.guildedmenu.gui.business.BusinessSelectMenu
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.SignShopIntegration
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.ChatUtils.sendSuccessMessage
import net.mcmerdith.guildedmenu.util.GMLogger
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*
import javax.annotation.Nonnull

object BusinessManager : CommandExecutor, TabCompleter {
    /*
    Business Command
     */
    override fun onCommand(
        @Nonnull sender: CommandSender,
        @Nonnull command: Command,
        @Nonnull label: String,
        @Nonnull args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendErrorMessage("Only players can use business commands")
            return true
        }

        if (IntegrationManager[SignShopIntegration::class.java]?.ready != true) {
            sender.sendErrorMessage("This command requires SignShop!")
            return true
        }

        if (args.isEmpty()) return false

        when (args[0]) {
            "register" -> {
                val name = args.slice(1..args.lastIndex).joinToString(" ")

                // Create and register
                Business.create(name, sender.uniqueId, null)?.apply {
                    register(this)
                    sender.sendSuccessMessage("Successfully registered '$name'")
                } ?: run {
                    sender.sendErrorMessage("Failed to register '$name'")
                }
            }

            "delete" -> {
                // Players can only delete their own businesses unless they are admins
                BusinessSelectMenu.getDeleteMenu(null, sender).get().open(sender)
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return if (args.size == 1) mutableListOf("register", "delete") else mutableListOf()
    }

    /*
    Business Manager
     */

    // Data Storage
    fun init() {
        val names = FileHandler.allBusinessIds

        for (name in names) {
            try {
                val b = Business.load(UUID.fromString(name)) ?: continue

                businessMap[b.id!!] = b
            } catch (e: IllegalArgumentException) {
                GMLogger.FILE.error("Error loading business '$name': Invalid ID", e)
            }
        }
    }

    private val businessMap: MutableMap<UUID, Business> = HashMap()

    fun find(id: UUID) = businessMap[id]

    fun allBusinesses() = businessMap.values

    fun register(business: Business) {
        businessMap[business.id!!] = business
    }

    fun deregister(business: Business) = businessMap.remove(business.id!!)
}