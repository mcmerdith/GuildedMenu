package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.framework.PlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.slot.SlotSettings
import java.util.*
import java.util.function.BiConsumer

/**
 * Menu to select a player
 *
 * If [online] is true only online players will be displayed
 *
 * [callback] will be executed with the selecting player and the selected player
 */
class PlayerSelectMenu(
    parent: Menu? = null,
    private val online: Boolean = false,
    private val callback: BiConsumer<Player, OfflinePlayer>
) : PaginatedMenu {
    private val template: BaseMenu.Builder = BaseMenu.Builder(6).title("Player Select").redraw(true).parent(parent)

    /**
     * A list of [Menu]s with containing all valid players
     */
    private val pages: List<Menu> = GuiUtil.getPagination(template, GuiUtil.getFullRowMask(5))
        .apply {
            var players = Arrays.stream(Bukkit.getOfflinePlayers())

            if (online) players = players.filter { it.isOnline }

            players.forEach { player ->
                // Add each player
                addItem(SlotSettings.builder()
                    .clickHandler { clickPlayer, _ ->
                        // Execute the callback when clicked
                        callback.accept(clickPlayer, player)
                    }
                    .itemTemplate(PlayerHeadItemTemplate.of(player))
                    .build())
            }
        }.build()

    override fun get(): Menu {
        return pages.first()
    }
}