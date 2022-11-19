package net.mcmerdith.guildedmenu.gui.framework

import org.ipvp.canvas.Menu

interface PaginatedMenu {
    /**
     * Get the first page (aka pagination entry point)
     */
    fun get(): Menu
}