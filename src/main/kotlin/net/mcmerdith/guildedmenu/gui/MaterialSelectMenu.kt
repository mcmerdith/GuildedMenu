package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.util.Filter
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.MenuSelectReceiver
import net.mcmerdith.guildedmenu.util.getHandler
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings

/**
 * Menu to select all [Material]s matching [filter]
 *
 * [selectReceiver] will be executed with the selecting player and the selected material
 */
class MaterialSelectMenu(
    private val previous: MenuProvider? = null,
    private val filter: Filter<Material> = { true },
    private val selectReceiver: MenuSelectReceiver<Material>
) : PaginatedMenu() {
    override fun getBuilder() = BaseMenu.Builder(6).title("Material Select").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(5)

    override fun setup(builder: PaginatedMenuBuilder) {
        Material.values().filter { !it.isAir && it.isItem && filter.invoke(it) }
            .forEach { material ->
                // Add each material
                builder.addItem(
                    SlotSettings.builder()
                        .clickHandler(selectReceiver.getHandler(material))
                        .item(ItemStack(material))
                        .build()
                )
            }
    }
}