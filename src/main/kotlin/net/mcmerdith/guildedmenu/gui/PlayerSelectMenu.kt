package net.mcmerdith.guildedmenu.gui

import dev.dbassett.skullcreator.SkullCreator
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings
import org.ipvp.canvas.type.ChestMenu
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

class PlayerSelectMenu(previous: Menu?, online: Boolean = false, callback: BiConsumer<Player, OfflinePlayer>) {
    val TEMPLATE = ChestMenu.builder(6).title("Player Select").redraw(true).parent(previous)

    val pages: List<Menu>

    init {
        pages = PaginatedMenuBuilder.builder(TEMPLATE)
            .nextButton(ItemTemplates.NEXT_BUTTON)
            .nextButtonSlot(GuiUtil.NEXT_MASK)
            .previousButton(ItemTemplates.PREV_BUTTON)
            .previousButtonSlot(GuiUtil.PREV_MASK)
            .apply {
                val players = Arrays.stream(Bukkit.getOfflinePlayers())

                if (online) players.filter { it.isOnline }

                for (player in players) {
                    addItem(SlotSettings.builder()
                        .clickHandler { clickPlayer, _ -> callback.accept(clickPlayer, player) }
                        .item(SkullCreator.itemFromUuid(player.uniqueId)).build())
                }
            }.build()
    }

    fun get(): Menu {
        return pages.first()
    }
}