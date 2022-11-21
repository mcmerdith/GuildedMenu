package net.mcmerdith.guildedmenu.gui.framework

import net.mcmerdith.guildedmenu.GuildedMenu
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.ipvp.canvas.Menu.Dimension
import org.ipvp.canvas.type.AbstractMenu
import org.ipvp.canvas.type.ChestMenu

/**
 * A [size] dimension [ChestMenu] with [title] as the title
 *
 * When closed [previous] will be opened (if provided)
 *
 * Use [redraw] for [PaginatedMenu]s
 */
class BaseMenu private constructor(
    private val title: String,
    private val size: Dimension,
    val previous: MenuProvider?,
    redraw: Boolean = false
) : ChestMenu(
    title,
    size.area,
    null,
    redraw
) {
    override fun closedByPlayer(viewer: Player?, triggerCloseHandler: Boolean) {
        super.closedByPlayer(viewer, triggerCloseHandler)

        previous?.apply {
            if (viewer != null && triggerCloseHandler) {
                Bukkit.getScheduler().runTask(GuildedMenu.plugin) { ->
                    if (
                        viewer.openInventory.type == InventoryType.CRAFTING ||
                        viewer.openInventory.type == InventoryType.CREATIVE
                    ) {
                        // Only reopen parent inventory if player does not have another open inventory
                        get().open(viewer)
                    }
                }
            }
        }
    }

    /**
     * Open for [player] after [ticks]
     */
    fun openLater(player: Player, ticks: Long = 2) {
        Bukkit.getScheduler().runTaskLater(GuildedMenu.plugin, { ->
            open(player)
        }, ticks)
    }

    class Builder(rows: Int) : AbstractMenu.Builder<Builder>(MenuSize(rows)) {
        private var previous: MenuProvider? = null

        fun previous(menu: MenuProvider?): Builder {
            this.previous = menu
            return this
        }

        override fun build(): BaseMenu {
            return BaseMenu(title, dimensions, previous, isRedraw)
        }
    }
}