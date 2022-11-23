package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.framework.StaticPlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.util.Filter
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.MenuSelectReceiver
import net.mcmerdith.guildedmenu.util.getHandler
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings

/**
 * Menu to select a player filtered by [filter]
 *
 * If [online] is true only online players will be displayed
 *
 * [selectReceiver] will be executed with the selecting player and the selected player
 */
class PlayerSelectMenu(
    private val previous: MenuProvider? = null,
    private val online: Boolean = false,
    private val filter: Filter<OfflinePlayer> = { true },
    private val selectReceiver: MenuSelectReceiver<OfflinePlayer>
) : PaginatedMenu() {
    override fun getBuilder() = BaseMenu.Builder(6).title("Player Select").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(5)

    override fun setup(builder: PaginatedMenuBuilder) {
        // Get all players filtered by the predicate
        val players = listOf(*Bukkit.getOfflinePlayers()).filter {
            filter.invoke(it) && (!online || it.isOnline)
        }

        players.forEach { player ->
            // Add each player
            builder.addItem(
                SlotSettings.builder()
                    .clickHandler(selectReceiver.getHandler(player))
                    .itemTemplate(StaticPlayerHeadItemTemplate.of(player))
                    .build()
            )
        }
    }
}