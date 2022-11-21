package net.mcmerdith.guildedmenu.gui

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.framework.ConditionalSlot
import net.mcmerdith.guildedmenu.gui.framework.MenuProvider
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor

/**
 * Player Balance Menu
 *
 * If [business] is not provided the menu will render for the current viewer
 */
class ViewBusinessMenu(
    private val previous: MenuProvider? = null,
    private val business: Business
) : BasicMenu() {
    override fun getBuilder(): BaseMenu.Builder = BaseMenu.Builder(3)
        .title(business.name ?: "Unnamed business")
        .previous(previous)

    override fun setup(menu: BaseMenu) {
        menu.apply {
            /*
            Manager Functions
             */

            // Rename
            getSlot(1, 1).settings = ConditionalSlot.build(
                ItemTemplates.UI.getEdit("Rename ${business.name}"),
                { p -> business.isManager(p) },
                { p, _ ->
                    GuiUtil.getAnvilGUIBuilder(
                        "Rename '${business.name}'",
                        business.getIcon(),
                        this@ViewBusinessMenu
                    ) { _, input ->
                        if (input.isBlank()) AnvilGUI.Response.text(business.name)
                        else {
                            business.name = input
                            business.save()

                            AnvilGUI.Response.close()
                        }
                    }.text(business.name).open(p)
                }
            )

            // Change Icon
            getSlot(2, 1).settings = ConditionalSlot.build(
                business.getIcon().setLore("Click to change icon"),
                { p -> business.isManager(p) },
                { p, _ ->
                    MaterialSelectMenu(this@ViewBusinessMenu) { _, material ->
                        business.icon = material
                        business.save()
                        true
                    }.get().open(p)
                }
            )

            // Remove Icon
            getSlot(3, 1).settings = ConditionalSlot.build(
                ItemTemplates.UI.getXMark("Reset Icon"),
                { p -> business.isManager(p) },
                { p, _ ->
                    business.icon = null
                    business.save()

                    get().open(p)
                }
            )

            /*
            Locations
             */

            getSlot(2, 3).apply {
                item = ItemTemplates.getSignshop("See locations")
                // clickHandler
            }

            /*
            Ownership
             */

            // Change owner
            getSlot(1, 5).settings = ConditionalSlot.build(
                ItemTemplates.UI.getTransfer("Transfer Ownership").setLore(
                    "Warning! You will no longer be able to",
                    "manage this business unless granted access",
                    "by the new owner!",
                    "${ChatColor.RED}This cannot be undone"
                ),
                { p -> business.isOwner(p) },
                { p, _ ->
                    PlayerSelectMenu(this@ViewBusinessMenu) { _, selected ->
                        business.owner = selected.uniqueId
                        business.save()
                        true
                    }.get().open(p)
                }
            )

            // Current owner
            getSlot(2, 5).item = SkullCreator.itemFromUuid(business.owner!!).setLore("Owner")

            /*
            Managers
             */

            getSlot(2, 7).apply {
                item = SkullCreator.createSkull().setName("View Managers")
                openOnClick(ManagerMenu(this@ViewBusinessMenu, business))
            }
        }
    }

}