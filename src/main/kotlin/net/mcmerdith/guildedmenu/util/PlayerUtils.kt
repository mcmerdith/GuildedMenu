package net.mcmerdith.guildedmenu.util

import com.palmergames.bukkit.towny.`object`.Resident
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Suppress("unused")
object PlayerUtils {
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

    /**
     * If the player is either OP or has the admin permission
     */
    fun CommandSender.isAdmin() = isOp || hasPermission(Permissions.ADMIN)
}