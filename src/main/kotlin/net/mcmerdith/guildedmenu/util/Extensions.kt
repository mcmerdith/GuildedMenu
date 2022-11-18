package net.mcmerdith.guildedmenu.util

import com.palmergames.bukkit.towny.`object`.Resident
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
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
}