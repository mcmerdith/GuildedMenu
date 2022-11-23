package net.mcmerdith.guildedmenu.gui.towny

import com.palmergames.bukkit.towny.`object`.Town
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import net.mcmerdith.guildedmenu.integration.TownyIntegration.Companion.getIcon
import net.mcmerdith.guildedmenu.util.Filter
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.MenuSelectReceiver
import net.mcmerdith.guildedmenu.util.getHandler
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings

class TownBrowserMenu(
    private val previous: MenuProvider?,
    private val filter: Filter<Town> = { true },
    private val selectReceiver: MenuSelectReceiver<Town>? = null
) : PaginatedMenu() {
    val towny by lazy { IntegrationManager[TownyIntegration::class.java]!! }

    override fun getBuilder() = BaseMenu.Builder(6).title("Town Browser").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(5)

    override fun setup(builder: PaginatedMenuBuilder) {
        for (town in towny.getTowns().filter(filter)) {
            builder.addItem(
                SlotSettings.builder()
                    .item(town.getIcon())
                    .clickHandler(selectReceiver?.getHandler(town)).build()
            )
        }
    }
}