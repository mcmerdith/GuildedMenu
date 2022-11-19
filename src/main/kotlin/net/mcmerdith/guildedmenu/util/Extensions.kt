package net.mcmerdith.guildedmenu.util

import com.palmergames.bukkit.towny.`object`.Resident
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import net.mcmerdith.guildedmenu.util.Extensions.addLore
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object Extensions {
    /**
     * Convert a player to a Towny resident
     *
     * Returns null if Towny is not available, or the player is not a resident
     */
    fun Player.asTownyResident(): Resident? = this.uniqueId.asTownyResident()

    /**
     * Convert a player to a Towny resident
     *
     * Returns null if Towny is not available, or the player is not a resident
     */
    fun UUID.asTownyResident(): Resident? {
        if (!IntegrationManager.has(TownyIntegration::class.java)) return null

        val towny = IntegrationManager[TownyIntegration::class.java] ?: return null

        return towny.getAPI().getResident(this)
    }

    /**
     * Convert the UUID to a [Player]
     *
     * Returns null if the player is not online
     */
    fun UUID.asPlayer(): Player? = Bukkit.getPlayer(this)

    /**
     * Convert the UUID to an [OfflinePlayer]
     */
    fun UUID.asOfflinePlayer(): OfflinePlayer = Bukkit.getOfflinePlayer(this)

    /**
     * Set the item display name to [name]
     */
    fun ItemStack.name(name: String? = null): ItemStack {
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

    fun ItemStack.getLore(): List<String> {
        return itemMeta?.lore ?: emptyList()
    }
}