package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.framework.PlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.util.Globals
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.slot.SlotSettings
import java.util.*
import java.util.function.BiConsumer

class PlayerSelectMenu(parent: Menu? = null, online: Boolean = false, callback: BiConsumer<Player, OfflinePlayer>) :
    PaginatedMenu {
    val TEMPLATE = BaseMenu.Builder(6).title("Player Select").redraw(true).parent(parent)

    val pages: List<Menu> = GuiUtil.getPagination(TEMPLATE, GuiUtil.getFullRowMask(5))
        .apply {
            // debug
            val dPlayers = Bukkit.getOfflinePlayers()
            val aPlayers = Array(128) { Globals.DEBUG_PLAYER }

            // production
//                var players = Arrays.stream(Bukkit.getOfflinePlayers())
            var players = Arrays.stream(dPlayers + aPlayers)

            if (online) players = players.filter { it.isOnline }

            players.forEach { player ->
                addItem(SlotSettings.builder()
                    .clickHandler { clickPlayer, _ ->
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