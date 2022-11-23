package net.mcmerdith.guildedmenu.gui.mythic

import net.Indyuce.mmoitems.api.Type
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.MMOItemsIntegration
import net.mcmerdith.guildedmenu.util.MenuProvider
import org.bukkit.entity.Player
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings

class MMOItemSelectMenu(private val previous: MenuProvider?, private val type: Type? = null) : PaginatedMenu() {
    private val mmoitems = IntegrationManager[MMOItemsIntegration::class.java]!!

    override fun getBuilder() =
        BaseMenu.Builder(6).title("MMO Item Spawner - Select ${if (type == null) "Type" else "Item"}").redraw(true)
            .previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(5)

    override fun setup(builder: PaginatedMenuBuilder) {
        if (type == null) {
            // Type selection
            for (type in mmoitems.types) {
                builder.addItem(
                    SlotSettings.builder()
                        .item(mmoitems.getTypeIcon(type))
                        .clickHandler { player, _ ->
                            MMOItemSelectMenu(this@MMOItemSelectMenu, type).get().open(player)
                        }.build()
                )
            }
        } else {
            for (item in mmoitems.getItems(type)) {
                val builderProvider = { player: Player -> item.newBuilder(player).build().newBuilder() }

                builder.addItem(
                    SlotSettings.builder()
                        .itemTemplate { player ->
                            builderProvider(player).build(true)
                        }.clickHandler { player, _ ->
                            player.inventory.addItem(builderProvider(player).build())
                            player.updateInventory()
                        }.build()
                )
            }
        }
    }
}