package net.mcmerdith.guildedmenu.gui

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.util.GMLogger
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings
import org.ipvp.canvas.type.ChestMenu
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

class PlayerSelectMenu(previous: Menu?, online: Boolean = false, callback: BiConsumer<Player, OfflinePlayer>) {
    val TEMPLATE = BaseMenu.Builder(6).title("Player Select").redraw(true).parent(previous)

    val pages: List<Menu>

    init {
        pages = PaginatedMenuBuilder.builder(TEMPLATE)
//            .slots(BinaryMask.builder(MenuSize(6)).pattern("111111111").build())
            .slots(GuiUtil.ALL_MASK)
            .nextButton(ItemTemplates.NEXT_BUTTON)
//            .nextButtonSlot(GuiUtil.NEXT_MASK)
            .nextButtonSlot(GuiUtil.getSlotNumber(6, 9))
            .previousButton(ItemTemplates.PREV_BUTTON)
//            .previousButtonSlot(GuiUtil.PREV_MASK)
            .previousButtonSlot(GuiUtil.getSlotNumber(6, 1))
            .apply {
                var players = Arrays.stream(Bukkit.getOfflinePlayers())

                if (online) players = players.filter { it.isOnline }

                players.forEach { player ->
                    GMLogger.MAIN.info("Adding player " + player.name)
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