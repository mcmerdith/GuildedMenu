package net.mcmerdith.guildedmenu.gui.util

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.MainMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuSize
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.Globals
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.Menu
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.Slot
import org.ipvp.canvas.type.AbstractMenu
import java.util.function.BiFunction

object GuiUtil : CommandExecutor {
    fun getFullRowMask(rows: Int): BinaryMask = getRowMask(rows, "111111111")

    fun getRowMask(rows: Int, pattern: String): BinaryMask {
        val builder = BinaryMask.builder(MenuSize(rows))

        repeat(rows) {
            @Suppress("DEPRECATION")
            builder.pattern(pattern)
        }

        return builder.build()
    }

    fun getSlotIndex(row: Int, column: Int): Int = (row - 1) * 9 + column - 1

    fun getPagination(builder: AbstractMenu.Builder<*>, mask: BinaryMask) = PaginatedMenuBuilder.builder(builder)
        .slots(mask)
        .previousButton(ItemTemplates.PREV_BUTTON)
        .previousButtonEmpty(ItemTemplates.PREV_BUTTON_DISABLED)
        .previousButtonSlot(getSlotIndex(6, 1))
        .nextButton(ItemTemplates.NEXT_BUTTON)
        .nextButtonEmpty(ItemTemplates.NEXT_BUTTON_DISABLED)
        .nextButtonSlot(getSlotIndex(6, 9))

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

    fun getAnvilGUIBuilder(
        title: String,
        item: ItemStack,
        parent: Menu? = null,
        callback: BiFunction<Player, String, AnvilGUI.Response>
    ): AnvilGUI.Builder = AnvilGUI.Builder()
        .onClose {
            Bukkit.getScheduler().runTask(GuildedMenu.plugin) { -> parent?.open(it) }
        }
        .onComplete(callback)
        .itemLeft(item)
        .title(title)
        .plugin(GuildedMenu.plugin)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender as? Player ?: run {
            sender.sendErrorMessage("In game menu can only be accessed by players!")
            return true
        }

        MainMenu(sender.isOp || sender.hasPermission(Globals.PERMISSION.ADMIN)).open(sender)
        return true
    }
}