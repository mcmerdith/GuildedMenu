package net.mcmerdith.guildedmenu.gui.business

import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.gui.PlayerSelectMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.ConditionalSlot
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.framework.StaticPlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.ItemStackUtils.addLore
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.MenuSelectReceiver
import net.mcmerdith.guildedmenu.util.PlayerUtils.asOfflinePlayer
import org.bukkit.ChatColor
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings
import java.util.*

/**
 * View managers of [business]
 */
class BusinessManagerMenu(
    private val previous: MenuProvider?,
    private val business: Business,
    private val delete: Boolean = false,
    private val selectReceiver: MenuSelectReceiver<UUID>? = null
) : PaginatedMenu() {
    override fun getBuilder() =
        BaseMenu.Builder(2).title("Managers (${business.name})").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(1)

    override fun setup(builder: PaginatedMenuBuilder) {
        builder.apply {
            business.managers.forEach { manager ->
                addItem(
                    SlotSettings.builder().itemTemplate(
                        StaticPlayerHeadItemTemplate.of(manager.asOfflinePlayer()) { item ->
                            if (delete) item.addLore("${ChatColor.RED}Remove this manager")
                        }
                    ).clickHandler { clickPlayer, _ ->
                        // Execute the callback when clicked
                        if (selectReceiver?.invoke(clickPlayer, manager) == true) clickPlayer.closeInventory()
                    }.build()
                )
            }

            // Don't show controls in delete mode
            if (delete) return

            // Add/Remove controls are only shown for the owner
            newMenuModifier { menu ->
                // Add manager button
                menu.getSlot(2, 4).settings = ConditionalSlot.build(
                    ItemTemplates.UI.getNew("Add a manager"),
                    { p -> business.isOwner(p) },
                    { p, _ ->
                        // Open a selection menu containing the players who are NOT currently managers
                        PlayerSelectMenu(
                            this@BusinessManagerMenu,
                            false,
                            { !business.managers.contains(it.uniqueId) }
                        ) { _, newManager ->
                            // Add and save
                            business.managers.add(newManager.uniqueId)
                            business.save()
                            true
                        }.get().open(p)
                    }
                )

                // Remove manager button
                menu.getSlot(2, 6).settings = ConditionalSlot.build(
                    ItemTemplates.UI.getDelete("Remove a manager"),
                    { p -> business.isOwner(p) },
                    { p, _ ->
                        // Open a delete menu for the managers
                        BusinessManagerMenu(
                            this@BusinessManagerMenu,
                            business,
                            true
                        ) { _, newManager ->
                            // Remove and save
                            business.managers.remove(newManager)
                            business.save()
                            true
                        }.get().open(p)
                    }
                )
            }
        }
    }
}