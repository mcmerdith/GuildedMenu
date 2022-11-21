package net.mcmerdith.guildedmenu.gui

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.business.BusinessManager
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuProvider
import net.mcmerdith.guildedmenu.gui.framework.MenuSelectReceiver
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.ChatUtils.sendSuccessMessage
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.PlayerUtils.asOfflinePlayer
import net.mcmerdith.guildedmenu.util.PlayerUtils.isAdmin
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings
import java.util.function.Predicate

/**
 * Menu to view businesses that match [filter] (all by default)
 *
 * [callback] will be executed with the selecting player and the selected business
 *
 * If [callback] is not provided a [ViewBusinessMenu] will be opened instead
 */
class BusinessSelectMenu(
    private val previous: MenuProvider? = null,
    private val filter: Predicate<Business> = Predicate<Business> { true },
    private val delete: Boolean = false,
    private val callback: MenuSelectReceiver<Business>? = null
) : PaginatedMenu() {
    companion object {
        /**
         * Open a [ViewBusinessMenu] for [business] on [player]
         */
        private fun defaultBehavior(parent: MenuProvider?, player: Player, business: Business) {
            ViewBusinessMenu(parent, business).get().open(player)
        }

        /**
         * Get a delete menu with a confirmation dialog
         */
        fun getDeleteMenu(previous: MenuProvider?, player: OfflinePlayer) =
            BusinessSelectMenu(
                previous,
                { (player is Player && player.isAdmin()) || it.owner == player.uniqueId },
                true
            ) { p, business ->
                val confirm = BaseMenu.Builder(1).title("Delete Business (confirm)").previous(previous).build()

                confirm.getSlot(1, 5).apply {
                    item = business.getIcon()
                        .setName("Delete ${business.name}")
                        .setLore("Are you sure?", "${ChatColor.RED}This cannot be undone")

                    setClickHandler { p, _ ->
                        business.delete()
                        p.sendSuccessMessage("Deleted ${business.name}")
                        p.closeInventory()
                    }
                }

                confirm.open(p)
                false
            }
    }

    override fun getBuilder() =
        BaseMenu.Builder(6).title("Select a Business").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(5)

    override fun setup(builder: PaginatedMenuBuilder) {
        builder.apply {
            BusinessManager.allBusinesses().stream().filter(filter).forEach { business ->
                // Add each business
                addItem(SlotSettings.builder()
                    .clickHandler { clickPlayer, clickInfo ->
                        if (clickInfo.clickType.isRightClick && !delete) {
                            // Filter by owner when right-clicked (not available in delete mode)
                            BusinessSelectMenu(
                                previous,
                                { it.owner == business.owner },
                                false,
                                callback
                            ).get().open(clickPlayer)
                        } else {
                            // Execute the callback when left-clicked (or default behavior)
                            callback?.apply {
                                if (invoke(clickPlayer, business)) clickPlayer.closeInventory()
                            } ?: defaultBehavior(
                                this@BusinessSelectMenu,
                                clickPlayer,
                                business
                            )
                        }
                    }
                    .item(
                        business.getIcon()
                            .setLore(
                                "Owned by: ${business.owner?.asOfflinePlayer()?.name}",
                                if (delete) "${ChatColor.RED}Delete this business" else "Right-Click: Filter by owner"
                            )
                    )
                    .build())
            }

            // Add controls if we're not in delete mode
            if (!delete) {
                newMenuModifier { menu ->
                    // New Business
                    menu.getSlot(6, 3).apply {
                        item = ItemTemplates.NEW
                        setClickHandler { player, _ ->
                            GuiUtil.getAnvilGUIBuilder(
                                "New business",
                                SkullCreator.itemFromUuid(player.uniqueId),
                                this@BusinessSelectMenu
                            ) { _, input ->
                                if (input.isBlank()) AnvilGUI.Response.text("Enter a name")
                                else {
                                    val b = Business.create(input, player.uniqueId, null)
                                    if (b == null) AnvilGUI.Response.text("An error occurred")
                                    else {
                                        BusinessManager.register(b)

                                        // Open the view on the next tick (closing the anvil will go back to select screen)
                                        ViewBusinessMenu(this@BusinessSelectMenu, b).get().openLater(player, 5)

                                        AnvilGUI.Response.close()
                                    }
                                }
                            }.text("Enter a name").open(player)
                        }
                    }

                    // Reset filters
                    menu.getSlot(6, 5).apply {
                        item = ItemTemplates.REFRESH.setName("Reset Filters")
                        openOnClick(BusinessSelectMenu(previous, delete = false, callback = callback))
                    }

                    // Delete mode
                    menu.getSlot(6, 7).apply {
                        item = ItemTemplates.DELETE
                        setClickHandler { player, _ ->
                            getDeleteMenu(this@BusinessSelectMenu, player).get().open(player)
                        }
                    }
                }
            }
        }
    }
}
