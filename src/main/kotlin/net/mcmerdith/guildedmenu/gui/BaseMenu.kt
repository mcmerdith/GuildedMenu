package net.mcmerdith.guildedmenu.gui

import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.Menu.Dimension
import org.ipvp.canvas.type.AbstractMenu
import org.ipvp.canvas.type.ChestMenu

open class BaseMenu(title: String, size: Dimension, parent: Menu?) : ChestMenu(
    title,
    size.area,
    parent,
    true
) {
    override fun closedByPlayer(viewer: Player?, triggerCloseHandler: Boolean) {
        super.closedByPlayer(viewer, triggerCloseHandler)

        parent.ifPresent {
            if (viewer != null) it.open(viewer)
        }
    }

    class Builder constructor(rows: Int) : AbstractMenu.Builder<Builder>(MenuSize(rows)) {
        override fun build(): BaseMenu {
            return BaseMenu(title, dimensions, parent)
        }
    }
}