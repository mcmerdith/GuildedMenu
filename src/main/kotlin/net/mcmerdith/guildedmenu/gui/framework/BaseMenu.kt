package net.mcmerdith.guildedmenu.gui.framework

import net.mcmerdith.guildedmenu.GuildedMenu
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.ipvp.canvas.Menu
import org.ipvp.canvas.Menu.Dimension
import org.ipvp.canvas.type.AbstractMenu
import org.ipvp.canvas.type.ChestMenu

open class BaseMenu(title: String, size: Dimension, parent: Menu?, redraw: Boolean = false) : ChestMenu(
    title,
    size.area,
    parent,
    redraw
) {
    override fun closedByPlayer(viewer: Player?, triggerCloseHandler: Boolean) {
        super.closedByPlayer(viewer, triggerCloseHandler)

        parent.ifPresent {
            if (viewer != null && triggerCloseHandler) {
                Bukkit.getScheduler().runTask(GuildedMenu.plugin) { ->
                    // Only reopen parent inventory if player does not have another open inventory
                    if (viewer.openInventory.type == InventoryType.CRAFTING || viewer.openInventory.type == InventoryType.CREATIVE) it.open(
                        viewer
                    )
                }
            }
        }
    }

    class Builder(rows: Int) : AbstractMenu.Builder<Builder>(MenuSize(rows)) {
        override fun build(): BaseMenu {
            return BaseMenu(title, dimensions, parent, isRedraw)
        }
    }
}