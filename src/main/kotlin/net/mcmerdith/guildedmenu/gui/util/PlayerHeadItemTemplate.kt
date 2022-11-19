package net.mcmerdith.guildedmenu.gui.util

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.util.Extensions.setName
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.template.StaticItemTemplate

class PlayerHeadItemTemplate internal constructor(val player: OfflinePlayer, val rawItem: ItemStack) :
    StaticItemTemplate(rawItem) {
    companion object {
        fun of(player: OfflinePlayer): PlayerHeadItemTemplate {
            return PlayerHeadItemTemplate(
                player,
                SkullCreator.itemFromUuid(player.uniqueId).setName(player.name ?: "Unknown Player")
            )
        }
    }
}