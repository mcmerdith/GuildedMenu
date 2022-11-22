package net.mcmerdith.guildedmenu.gui.framework

import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.util.MenuProvider
import org.ipvp.canvas.Menu
import org.ipvp.canvas.mask.BinaryMask
import org.ipvp.canvas.paginate.PaginatedMenuBuilder

abstract class PaginatedMenu : MenuProvider {
    /**
     * The [BaseMenu.Builder] for this menu
     */
    abstract fun getBuilder(): BaseMenu.Builder

    /**
     * The [BinaryMask] identifying which slots may be filled by the pagination
     */
    abstract fun getRowMask(): BinaryMask

    /**
     * Called before [builder] is built
     *
     * Add pagination items here and menu modifiers here
     */
    abstract fun setup(builder: PaginatedMenuBuilder)

    /**
     * Menus from [getBuilder] will be passed to this function before being returned to the caller
     *
     * Add any additional items here
     */
    open fun setup(menus: List<Menu>) {}

    /**
     * Get the first page (aka pagination entry point)
     */
    final override fun get(): BaseMenu = GuiUtil.getPagination(getBuilder(), getRowMask())
        .apply { setup(this) }.build()
        .apply { setup(this) }.first() as BaseMenu
}