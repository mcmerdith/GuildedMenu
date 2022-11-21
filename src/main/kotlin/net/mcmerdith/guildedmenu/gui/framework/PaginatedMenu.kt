package net.mcmerdith.guildedmenu.gui.framework

import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import org.ipvp.canvas.Menu
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.paginate.PaginatedMenuBuilder

abstract class PaginatedMenu : MenuProvider {
    /**
     * The builder for this menu
     */
    abstract fun getBuilder(): BaseMenu.Builder

    abstract fun getRowMask(): BinaryMask

    abstract fun setup(builder: PaginatedMenuBuilder)

    /**
     * Menus from [getBuilder] will be passed to this function before being returned to the caller
     */
    open fun setup(menus: List<Menu>) {}

    /**
     * Get the first page (aka pagination entry point)
     */
    final override fun get(): BaseMenu = GuiUtil.getPagination(getBuilder(), getRowMask())
        .apply { setup(this) }.build()
        .apply { setup(this) }.first() as BaseMenu
}