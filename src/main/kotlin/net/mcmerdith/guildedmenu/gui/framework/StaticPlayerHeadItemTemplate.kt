package net.mcmerdith.guildedmenu.gui.framework

import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.PlayerUtils.getHeadItem
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.template.StaticItemTemplate

class StaticPlayerHeadItemTemplate internal constructor(
    val player: OfflinePlayer,
    val rawItem: ItemStack
) : StaticItemTemplate(rawItem) {
    companion object {
        /**
         * Construct a [StaticPlayerHeadItemTemplate] for [player]
         */
        fun of(player: OfflinePlayer): StaticPlayerHeadItemTemplate {
            return StaticPlayerHeadItemTemplate(
                player,
                player.getHeadItem().setName(player.name ?: "Unknown Player")
            )
        }
    }
}