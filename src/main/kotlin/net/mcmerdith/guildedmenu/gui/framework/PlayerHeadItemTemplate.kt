package net.mcmerdith.guildedmenu.gui.framework

import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.PlayerUtils.getHeadItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.template.ItemStackTemplate

/**
 * An [ItemStackTemplate] returning the head of the player with [modifier] applied if provided
 */
class PlayerHeadItemTemplate(private val modifier: ((ItemStack, Player?) -> ItemStack)? = null) : ItemStackTemplate {
    companion object {
        /**
         * No modifier
         */
        @Suppress("unused")
        val INST = PlayerHeadItemTemplate()
    }

    override fun getItem(player: Player?): ItemStack? {
        player?.getHeadItem()?.setName(player.name)?.apply {
            return modifier?.invoke(this, player) ?: this
        }

        return null
    }

}