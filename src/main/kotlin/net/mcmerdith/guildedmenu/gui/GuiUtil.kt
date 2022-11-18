package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.Globals
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.slot.Slot
import org.ipvp.canvas.type.AbstractMenu
import org.ipvp.canvas.type.ChestMenu
import java.util.*

object GuiUtil : CommandExecutor {
    val PREV_MASK = BinaryMask.builder(MenuSize(1)).pattern("100000000").build()
    val NEXT_MASK = BinaryMask.builder(MenuSize(1)).pattern("000000001").build()
    val ALL_MASK = BinaryMask.builder(MenuSize(1)).pattern("111111111").build()

    fun getSlotNumber(row: Int, column: Int): Int = (column - 1) * 9 + row - 1

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

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender as? Player ?: run {
            sender.sendErrorMessage("In game menu can only be accessed by players!")
            return true
        }

        MainMenu(sender.isOp || sender.hasPermission(Globals.PERMISSION_ADMIN)).open(sender)
        return true
    }
}