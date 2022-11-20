package net.mcmerdith.guildedmenu.gui

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuSize
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.framework.StaticPlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.Extensions.asOfflinePlayer
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.Extensions.setName
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.Menu

/**
 * Player Balance Menu
 *
 * If [business] is not provided the menu will render for the current viewer
 */
class ViewBusinessMenu(val parent: Menu? = null, private val business: Business) : BaseMenu(
    business.name ?: "Unnamed business",
    MenuSize(3),
    parent
) {
    fun regenerate() = ViewBusinessMenu(parent, business)

    init {
        /*
        Manager Functions
         */

        // Rename
        getSlot(1, 1).apply {
            setItemTemplate { p ->
                if (business.isManager(p)) ItemTemplates.EDIT.setLore("Edit business name")
                else null
            }
            setClickHandler { p, _ ->
                if (business.isManager(p)) {
                    GuiUtil.getAnvilGUIBuilder(
                        "Rename '${business.name}'",
                        business.getIcon(),
                        this@ViewBusinessMenu
                    ) { _, input ->
                        if (input.isBlank()) AnvilGUI.Response.text(business.name)
                        else {
                            business.name = input
                            business.save()
                            // Open the view on the next tick (closing the anvil will go back to select screen)
                            regenerate().openLater(p)
                            AnvilGUI.Response.close()
                        }
                    }.text(business.name).open(p)
                }
            }
        }

        // Change Icon
        getSlot(2, 1).apply {
            setItemTemplate { p ->
                if (business.isManager(p)) business.getIcon().setLore("Click to change icon")
                else null
            }
            setClickHandler { p, _ ->
                if (business.isManager(p)) {
                    MaterialSelectMenu(this@ViewBusinessMenu) { player, material ->
                        business.icon = material
                        business.save()

                        regenerate().open(player)
                    }.get().open(p)
                }
            }
        }

        // Remove Icon
        getSlot(3, 1).apply {
            setItemTemplate { p ->
                if (business.isManager(p)) ItemTemplates.XMARK.setLore("Click to reset icon")
                else null
            }
            setClickHandler { player, _ ->
                if (business.isManager(player)) {
                    business.icon = null
                    business.save()

                    regenerate().open(player)
                }
            }
        }

        /*
        Locations
         */

        getSlot(2, 3).apply {
            item = ItemStack(Material.OAK_SIGN).setName("See locations")
            // clickHandler
        }

        /*
        Ownership
         */

        // Change owner
        getSlot(1, 5).apply {
            setItemTemplate { p ->
                if (business.isManager(p)) {
                    val i = ItemTemplates.TRANSFER.setName("Transfer Ownership")

                    // Only the owner can change ownership
                    if (business.isOwner(p))
                        i.setLore(
                            "Warning! You will no longer be able to",
                            "manage this business unless granted access",
                            "by the new owner!",
                            "${ChatColor.RED}This cannot be undone"
                        )
                    else i.setLore(
                        "You do not have permission to",
                        "perform this action"
                    )
                } else null
            }
            setClickHandler { p, _ ->
                PlayerSelectMenu(this@ViewBusinessMenu) { _, selected ->
                    business.owner = selected.uniqueId
                    business.save()
                    regenerate().open(p)
                }.get().open(p)
            }
        }

        // Current owner
        getSlot(2, 5).item = SkullCreator.itemFromUuid(business.owner!!).setLore("Owner")

        /*
        Managers
         */

        getSlot(2, 7).apply {
            item = SkullCreator.createSkull().setName("View Managers")
            GuiUtil.openScreenSupplierOnClick(this, ManagerMenu(this@ViewBusinessMenu, business)::get)
        }
    }

    class ManagerMenu(private val parent: Menu?, private val business: Business) : PaginatedMenu {
        private val template: Builder = Builder(2).title("Managers (${business.name})").redraw(true).parent(parent)

        override fun regenerate() = ManagerMenu(parent, business).get()

        /**
         * A list of [Menu]s
         */
        private val pages: List<Menu>
            get() = GuiUtil.getPagination(template, GuiUtil.getFullRowMask(1)).apply {
                business.managers.forEach { manager ->
                    addItem(StaticPlayerHeadItemTemplate.of(manager.asOfflinePlayer()))
                }

                newMenuModifier { menu ->
                    menu.getSlot(2, 4).apply {
                        setItemTemplate { p ->
                            if (business.isManager(p)) ItemTemplates.NEW.setLore("Add a manager")
                            else null
                        }
                        setClickHandler { p, _ ->
                            if (business.isManager(p)) {
                                PlayerSelectMenu(
                                    menu,
                                    false,
                                    mutableListOf(*Bukkit.getOfflinePlayers())
                                        .filter { !business.managers.contains(it.uniqueId) }
                                ) { player, newManager ->
                                    business.managers.add(newManager.uniqueId)
                                    business.save()

                                    regenerate().open(player)
                                }.get().open(p)
                            }
                        }
                    }

                    menu.getSlot(2, 6).apply {
                        setItemTemplate { p ->
                            if (business.isManager(p)) ItemTemplates.DELETE.setLore("Remove a manager")
                            else null
                        }
                        setClickHandler { p, _ ->
                            if (business.isManager(p)) {
                                PlayerSelectMenu(
                                    menu,
                                    false,
                                    business.managers.map { it.asOfflinePlayer() }
                                ) { player, newManager ->
                                    business.managers.remove(newManager.uniqueId)
                                    business.save()

                                    regenerate().open(player)
                                }.get().open(p)
                            }
                        }
                    }
                }
            }.build()

        override fun get(): Menu = pages.first()
    }
}