package net.mcmerdith.guildedmenu.integration

import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class EssentialsIntegration : Integration("Essentials") {
    override fun onEnable(): Boolean {
        return true
    }

    private fun getTPAExecutor(tpahere: Boolean) =
        fun(player: Player, target: OfflinePlayer): Boolean {
            if (player.uniqueId == target.uniqueId) {
                player.sendErrorMessage("You can't TPA to yourself!")
                return false
            }

            return if (target.isOnline) {
                player.performCommand("tpa${if (tpahere) "here" else ""} ${target.name}")
                true
            } else {
                player.sendErrorMessage("Could not TPA! (is the target online?)")
                false
            }
        }

    fun getTPAExecutor() = getTPAExecutor(false)

    fun getTPAHereExecutor() = getTPAExecutor(true)
}