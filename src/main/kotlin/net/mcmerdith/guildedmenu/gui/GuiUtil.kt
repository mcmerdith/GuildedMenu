package net.mcmerdith.guildedmenu.gui

import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.slot.Slot
import org.ipvp.canvas.type.AbstractMenu
import org.ipvp.canvas.type.ChestMenu
import java.util.*

object GuiUtil {
    val PREV_MASK = BinaryMask.builder(MenuSize(1)).pattern("100000000").build()
    val NEXT_MASK = BinaryMask.builder(MenuSize(1)).pattern("000000001").build()

    /**
     * Get a [menu] with an optional [previous] menu that opens when the main [menu] is closed
     */
    private fun getMenu(menu: AbstractMenu.Builder<*>, parent: Menu?): Menu =
        menu.apply { if (parent != null) parent(parent) }.build()

    /**
     * When [slot] is clicked open the [leftClick] menu.
     *
     * If the player right clicks and [rightClick] is provided [rightClick] will be opened instead of [leftClick]
     */
    fun openScreenOnClick(slot: Slot, leftClick: Menu, rightClick: Menu? = null) {
        slot.setClickHandler { player, clickInfo ->
            val type = clickInfo.clickType

            if (rightClick != null && type.isRightClick) rightClick.open(player)
            else leftClick.open(player)
        }
    }
}