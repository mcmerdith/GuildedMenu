package net.mcmerdith.guildedmenu.gui.util

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.AdminMenu
import net.mcmerdith.guildedmenu.gui.MainMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuProvider
import net.mcmerdith.guildedmenu.gui.framework.MenuSize
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.Extensions.isAdmin
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.Slot
import org.ipvp.canvas.type.AbstractMenu
import java.util.function.BiFunction

object GuiUtil : CommandExecutor {
    /**
     * Get a [BinaryMask] with [rows] rows
     *
     * Every row will have all slots masked
     */
    fun getFullRowMask(rows: Int): BinaryMask = getRowMask(rows, "111111111")

    /**
     * Get a [BinaryMask] with [rows] rows
     *
     * [pattern] will be repeated on every row
     */
    fun getRowMask(rows: Int, pattern: String): BinaryMask {
        val builder = BinaryMask.builder(MenuSize(rows))

        repeat(rows) {
            @Suppress("DEPRECATION")
            builder.pattern(pattern)
        }

        return builder.build()
    }

    /**
     * Convert [row] and [column] (both 1-indexed) into a slot index
     */
    fun getSlotIndex(row: Int, column: Int): Int = (row - 1) * 9 + column - 1

    /**
     * Get a [PaginatedMenuBuilder] with [builder] as the template
     *
     * [mask] defines where the paginated items will be placed
     *
     * Previous/Next buttons will be in columns 1 and 9 of the last row of [builder]
     */
    fun getPagination(builder: AbstractMenu.Builder<*>, mask: BinaryMask): PaginatedMenuBuilder =
        PaginatedMenuBuilder.builder(builder)
            .slots(mask)
            .previousButton(ItemTemplates.PREV_BUTTON)
            .previousButtonEmpty(ItemTemplates.PREV_BUTTON_DISABLED)
            .previousButtonSlot(getSlotIndex(builder.dimensions.rows, 1))
            .nextButton(ItemTemplates.NEXT_BUTTON)
            .nextButtonEmpty(ItemTemplates.NEXT_BUTTON_DISABLED)
            .nextButtonSlot(getSlotIndex(builder.dimensions.rows, 9))

    /**
     * When clicked open the [click] menu.
     *
     * If the player right clicks and [rightClick] is provided [rightClick] will be opened instead
     */
    fun Slot.openOnClick(click: MenuProvider, rightClick: MenuProvider? = null) {
        setClickHandler { player, clickInfo ->
            val type = clickInfo.clickType

            if (rightClick != null && type.isRightClick) rightClick.get().open(player)
            else click.get().open(player)
        }
    }

    /**
     * Get an [AnvilGUI.Builder] with a [title]
     *
     * [item] will be in the left slot
     *
     * When closed [parent] will be opened (if provided)
     *
     * [callback] will be called to process the input
     */
    fun getAnvilGUIBuilder(
        title: String,
        item: ItemStack,
        parent: MenuProvider? = null,
        callback: BiFunction<Player, String, AnvilGUI.Response>
    ): AnvilGUI.Builder = AnvilGUI.Builder()
        .onClose {
            parent?.get()?.run {
                openLater(it)
            }
        }
        .onComplete(callback)
        .itemLeft(item)
        .title(title)
        .plugin(GuildedMenu.plugin)

    /**
     * CommandHandler for [guildedmenu (gm), guildedmenuadmin (gma)]
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendErrorMessage("In game menu can only be accessed by players!")
            return true
        }

        val mainMenu = MainMenu(sender.isAdmin())

        if (label == "guildedmenuadmin" || label == "gma") {
            AdminMenu(mainMenu).get().open(sender)
        } else {
            mainMenu.get().open(sender)
        }

        return true
    }
}