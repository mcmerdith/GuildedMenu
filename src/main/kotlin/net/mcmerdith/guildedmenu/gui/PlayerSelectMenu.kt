package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.framework.StaticPlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.MenuSelectReceiver
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings
import java.util.*
import java.util.function.Predicate

/**
 * Menu to select a player filtered by [predicate]
 *
 * If [online] is true only online players will be displayed
 *
 * [selectReceiver] will be executed with the selecting player and the selected player
 */
class PlayerSelectMenu(
    private val previous: MenuProvider? = null,
    private val online: Boolean = false,
    private val predicate: Predicate<OfflinePlayer>? = null,
    private val selectReceiver: MenuSelectReceiver<OfflinePlayer>
) : PaginatedMenu() {
    override fun getBuilder() = BaseMenu.Builder(6).title("Player Select").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(5)

    override fun setup(builder: PaginatedMenuBuilder) {
        builder.apply {
            // Get all players filtered by the predicate
            var players = Arrays.stream(Bukkit.getOfflinePlayers())

            predicate?.let { players = players.filter(it) }
            if (online) players = players.filter { it.isOnline }

            players.forEach { player ->
                // Add each player
                addItem(SlotSettings.builder()
                    .clickHandler { clickPlayer, _ ->
                        // Execute the callback when clicked
                        if (selectReceiver.invoke(clickPlayer, player)) clickPlayer.closeInventory()
                    }
                    .itemTemplate(StaticPlayerHeadItemTemplate.of(player))
                    .build())
            }
        }
    }
}