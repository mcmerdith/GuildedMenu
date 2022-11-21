package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.gui.framework.*
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.PlayerUtils.asOfflinePlayer
import org.bukkit.Bukkit
import org.ipvp.canvas.paginate.PaginatedMenuBuilder

class ManagerMenu(private val previous: MenuProvider?, private val business: Business) : PaginatedMenu() {
    override fun getBuilder() =
        BaseMenu.Builder(2).title("Managers (${business.name})").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(1)

    override fun setup(builder: PaginatedMenuBuilder) {
        builder.apply {
            business.managers.forEach { manager ->
                addItem(StaticPlayerHeadItemTemplate.of(manager.asOfflinePlayer()))
            }

            newMenuModifier { menu ->
                menu.getSlot(2, 4).settings = ConditionalSlot.build(
                    ItemTemplates.UI.getNew("Add a manager"),
                    { p -> business.isOwner(p) },
                    { p, _ ->
                        PlayerSelectMenu(
                            this@ManagerMenu,
                            false,
                            mutableListOf(*Bukkit.getOfflinePlayers())
                                .filter { !business.managers.contains(it.uniqueId) }
                        ) { _, newManager ->
                            business.managers.add(newManager.uniqueId)
                            business.save()
                            true
                        }.get().open(p)
                    }
                )

                menu.getSlot(2, 6).settings = ConditionalSlot.build(
                    ItemTemplates.UI.getDelete("Remove a manager"),
                    { p -> business.isOwner(p) },
                    { p, _ ->
                        PlayerSelectMenu(
                            this@ManagerMenu,
                            false,
                            business.managers.map { it.asOfflinePlayer() }
                        ) { _, newManager ->
                            business.managers.remove(newManager.uniqueId)
                            business.save()
                            true
                        }.get().open(p)
                    }
                )
            }
        }
    }
}