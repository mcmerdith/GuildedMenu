package net.mcmerdith.guildedmenu.gui.framework

import org.ipvp.canvas.Menu

interface PaginatedMenu {
    /**
     * UI is pre-generated so changing the source set won't update it
     *
     * Make an identical copy of this menu but with new data
     */
    fun regenerate(): Menu

    /**
     * Get the first page (aka pagination entry point)
     */
    fun get(): Menu
}