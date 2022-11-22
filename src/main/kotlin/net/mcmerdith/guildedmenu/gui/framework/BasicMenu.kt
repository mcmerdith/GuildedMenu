package net.mcmerdith.guildedmenu.gui.framework

import net.mcmerdith.guildedmenu.util.MenuProvider

abstract class BasicMenu : MenuProvider {
    /**
     * The builder for this menu
     */
    abstract fun getBuilder(): BaseMenu.Builder

    /**
     * Menus from [getBuilder] will be passed to this function before being returned to the caller
     */
    abstract fun setup(menu: BaseMenu)

    /**
     * Get the first page (aka pagination entry point)
     */
    final override fun get(): BaseMenu = getBuilder().build().apply { setup(this) }
}