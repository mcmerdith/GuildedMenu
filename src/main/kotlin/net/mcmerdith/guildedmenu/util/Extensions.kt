package net.mcmerdith.guildedmenu.util

import com.palmergames.bukkit.towny.`object`.Resident
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object Extensions {

    /*
    Player Converters
     */

    /**
     * Convert a player to a Towny resident
     *
     * Returns null if Towny is not available, or the player is not a resident
     */
    fun Player.asTownyResident() = this.uniqueId.asTownyResident()

    /**
     * Convert a player to a Towny resident
     *
     * Returns null if Towny is not available, or the player is not a resident
     */
    fun UUID.asTownyResident(): Resident? {
        val towny = IntegrationManager[TownyIntegration::class.java]?.apply { if (!ready) return null } ?: return null

        return towny.getAPI().getResident(this)
    }

    /**
     * Convert the UUID to a [Player]
     *
     * Returns null if the player is not online
     */
    fun UUID.asPlayer() = Bukkit.getPlayer(this)

    /**
     * Convert the UUID to an [OfflinePlayer]
     */
    fun UUID.asOfflinePlayer() = Bukkit.getOfflinePlayer(this)

    /*
    Player Util Functions
     */

    /**
     * If the player is either OP or has the admin permission
     */
    fun CommandSender.isAdmin() = isOp || hasPermission(Globals.PERMISSION.ADMIN)

    /*
    ItemStack Util Functions
     */

    /**
     * Set the item display name to [name]
     */
    fun ItemStack.setName(name: String? = null): ItemStack {
        val meta = itemMeta ?: run {
            GMLogger.MAIN.warn("Attempted to set name for ${type.name}x$amount, which has no meta!")
            return this
        }

        // Blank will use default item name
        if (name.isNullOrBlank()) meta.setDisplayName(ChatColor.RESET.toString())
        else meta.setDisplayName(name)

        itemMeta = meta

        return this
    }

    /**
     * Get the item name
     */
    fun ItemStack.getName() = itemMeta?.displayName

    /**
     * Add [lore] to the item lore
     */
    fun ItemStack.addLore(vararg lore: String): ItemStack {
        val meta = itemMeta ?: run {
            GMLogger.MAIN.warn("Attempted to add lore to ${type.name}x$amount, which has no meta!")
            return this
        }

        return if (meta.lore == null) {
            setLore(*lore)
        } else {
            meta.lore!!.addAll(lore)
            itemMeta = meta

            this
        }
    }

    /**
     * Set the lore to [lore]
     */
    fun ItemStack.setLore(vararg lore: String): ItemStack {
        val meta = itemMeta ?: run {
            GMLogger.MAIN.warn("Attempted to set lore on ${type.name}x$amount, which has no meta!")
            return this
        }

        meta.lore = mutableListOf(*lore)

        itemMeta = meta

        return this
    }

    /**
     * Get the item lore
     */
    fun ItemStack.getLore() = itemMeta?.lore ?: emptyList()
}