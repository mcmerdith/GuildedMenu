package net.mcmerdith.guildedmenu.util

import com.palmergames.bukkit.towny.`object`.Resident
import com.palmergames.bukkit.towny.`object`.Town
import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

@Suppress("unused")
object PlayerUtils {
    /**
     * Convert a player to a Towny resident
     *
     * Returns null if Towny is not available, or the player is not a resident
     */
    fun OfflinePlayer.asTownyResident() = this.uniqueId.asTownyResident()

    /**
     * Get this players head as an ItemStack
     */
    fun OfflinePlayer.getHeadItem(): ItemStack = uniqueId.getPlayerHead()

    /**
     * If the player can edit [town]
     */
    fun OfflinePlayer.canInherentlyEdit(town: Town): Boolean = town.isMayor(asTownyResident())

    fun Player.canEdit(town: Town): Boolean = canInherentlyEdit(town) || isTownyAdmin()

    /**
     * Get the players head as an item
     */
    fun UUID.getPlayerHead(): ItemStack = SkullCreator.itemFromUuid(this)

    /**
     * Convert a player to a Towny resident
     *
     * Returns null if Towny is not available, or the player is not a resident
     */
    fun UUID.asTownyResident(): Resident? {
        val towny = IntegrationManager[TownyIntegration::class.java]?.apply { if (!ready) return null } ?: return null

        return towny.getResident(this)
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

    /**
     * If the player meets conditions for [isAdmin] or has the towny admin permission
     */
    fun CommandSender.isTownyAdmin() =
        isAdmin() || hasPermission(Permissions.TOWNYADMIN) || hasPermission(Permissions.TOWNYPLUGINADMIN)
}