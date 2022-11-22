package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.MenuSelectReceiver
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings
import java.util.*
import java.util.function.Predicate

/**
 * Menu to select all [Material]s matching [filter]
 *
 * [callback] will be executed with the selecting player and the selected material
 */
class MaterialSelectMenu(
    private val previous: MenuProvider? = null,
    private val filter: Predicate<Material>? = null,
    private val callback: MenuSelectReceiver<Material>
) : PaginatedMenu() {
    override fun getBuilder() = BaseMenu.Builder(6).title("Material Select").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(5)

    override fun setup(builder: PaginatedMenuBuilder) {
        builder.apply {
            Arrays.stream(Material.values()).filter { !it.isAir && it.isItem }
                .apply { if (filter != null) filter(filter) }.forEach { material ->
                    // Add each material
                    addItem(SlotSettings.builder()
                        .clickHandler { clickPlayer, _ ->
                            // Execute the callback when clicked
                            if (callback.invoke(clickPlayer, material)) clickPlayer.closeInventory()
                        }
                        .item(ItemStack(material))
                        .build())
                }
        }
    }
}