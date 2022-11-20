package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.Menu
import org.ipvp.canvas.slot.SlotSettings
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Predicate

/**
 * Menu to select a player
 *
 * If [online] is true only online players will be displayed
 *
 * [callback] will be executed with the selecting player and the selected player
 */
class MaterialSelectMenu(
    parent: Menu? = null,
    private val filter: Predicate<Material>? = null,
    private val callback: BiConsumer<Player, Material>
) : PaginatedMenu {
    private val template: BaseMenu.Builder = BaseMenu.Builder(6).title("Material Select").redraw(true).parent(parent)

    override fun regenerate() = this.get()

    /**
     * A list of [Menu]s with containing all valid players
     */
    private val pages: List<Menu>
        get() = GuiUtil.getPagination(template, GuiUtil.getFullRowMask(5))
            .apply {
                Arrays.stream(Material.values()).filter { !it.isAir && it.isItem }
                    .apply { if (filter != null) filter(filter) }.forEach { material ->
                        // Add each material
                        addItem(SlotSettings.builder()
                            .clickHandler { clickPlayer, _ ->
                                // Execute the callback when clicked
                                callback.accept(clickPlayer, material)
                            }
                            .item(ItemStack(material))
                            .build())
                    }
            }.build()

    override fun get(): Menu {
        return pages.first()
    }
}