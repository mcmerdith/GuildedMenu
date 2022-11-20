package net.mcmerdith.guildedmenu.gui

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.business.BusinessManager
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuSize
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.ChatUtils.sendSuccessMessage
import net.mcmerdith.guildedmenu.util.Extensions.asOfflinePlayer
import net.mcmerdith.guildedmenu.util.Extensions.isAdmin
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.Extensions.setName
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.slot.SlotSettings
import java.util.function.BiConsumer
import java.util.function.Predicate

/**
 * Menu to view businesses that match [filter] (all by default)
 *
 * [callback] will be executed with the selecting player and the selected business
 *
 * If [callback] is not provided a [ViewBusinessMenu] will be opened instead
 */
class BusinessSelectMenu(
    private val parent: Menu? = null,
    private val filter: Predicate<Business> = Predicate<Business> { true },
    private val delete: Boolean = false,
    private val callback: BiConsumer<Player, Business>? = null
) : PaginatedMenu {
    companion object {
        /**
         * Open a [ViewBusinessMenu] for [business] on [player]
         */
        private fun defaultBehavior(parent: Menu?, player: Player, business: Business) {
            ViewBusinessMenu(parent, business).open(player)
        }

        /**
         * Get a delete menu with a confirmation dialog
         */
        fun getDeleteMenu(parent: Menu?, player: OfflinePlayer) =
            BusinessSelectMenu(
                parent,
                { (player is Player && player.isAdmin()) || it.owner == player.uniqueId },
                true
            ) { p, business ->
                val confirm = BaseMenu("Delete Business (confirm)", MenuSize(1), parent, false)

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
            }.get()
    }

    private val template: BaseMenu.Builder = BaseMenu.Builder(6).title("Select a Business").redraw(true).parent(parent)

    override fun regenerate() = BusinessSelectMenu(parent, filter, delete, callback).get()

    /**
     * A list of [Menu]s with containing all valid players
     */
    private val pages: List<Menu>
        get() = GuiUtil.getPagination(
            template,
            GuiUtil.getFullRowMask(5)
        )
            .apply {
                BusinessManager.allBusinesses().stream().filter(filter).forEach { business ->
                    // Add each business
                    addItem(SlotSettings.builder()
                        .clickHandler { clickPlayer, clickInfo ->
                            if (clickInfo.clickType.isRightClick && !delete) {
                                // Filter by owner when right-clicked (not available in delete mode)
                                BusinessSelectMenu(parent, { it.owner == business.owner }, false, callback).get()
                                    .open(clickPlayer)
                            } else {
                                // Execute the callback when left-clicked (or default behavior)
                                callback?.accept(clickPlayer, business) ?: defaultBehavior(
                                    clickInfo.clickedMenu,
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
                                    menu
                                ) { _, input ->
                                    if (input.isBlank()) AnvilGUI.Response.text("Enter a name")
                                    else {
                                        val b = Business.create(input, player.uniqueId, null)
                                        if (b == null) AnvilGUI.Response.text("An error occurred")
                                        else {
                                            BusinessManager.register(b)
                                            // Open the view on the next tick (closing the anvil will go back to select screen)
                                            ViewBusinessMenu(regenerate(), b).openLater(player)
                                            AnvilGUI.Response.close()
                                        }
                                    }
                                }.text("Enter a name").open(player)
                            }
                        }

                        // Refresh
                        menu.getSlot(6, 5).apply {
                            item = ItemTemplates.REFRESH.setName("Refresh")
                            setClickHandler { player, _ -> regenerate().open(player) }
                        }

                        // Delete mode
                        menu.getSlot(6, 7).apply {
                            item = ItemTemplates.DELETE
                            setClickHandler { player, _ ->
                                getDeleteMenu(menu, player).open(player)
                            }
                        }
                    }
                }
            }.build()

    override fun get(): Menu {
        return pages.first()
    }
}
