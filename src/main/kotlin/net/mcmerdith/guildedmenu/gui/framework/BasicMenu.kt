package net.mcmerdith.guildedmenu.gui.framework

abstract class BasicMenu : MenuProvider {
    /**
     * The builder for this menu
     */
    abstract fun getBuilder(): MenuBase.Builder

    /**
     * Menus from [getBuilder] will be passed to this function before being returned to the caller
     */
    abstract fun setup(menu: MenuBase)

    /**
     * Get the first page (aka pagination entry point)
     */
    final override fun get(): MenuBase = getBuilder().build().apply { setup(this) }
}