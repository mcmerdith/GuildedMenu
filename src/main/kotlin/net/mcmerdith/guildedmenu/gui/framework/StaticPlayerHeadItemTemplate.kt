package net.mcmerdith.guildedmenu.gui.framework

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.util.Extensions.setName
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
                SkullCreator.itemFromUuid(player.uniqueId).setName(player.name ?: "Unknown Player")
            )
        }
    }
}