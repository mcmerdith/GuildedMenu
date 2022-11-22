package net.mcmerdith.guildedmenu.gui.business

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.gui.MaterialSelectMenu
import net.mcmerdith.guildedmenu.gui.PlayerSelectMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.framework.ConditionalSlot
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.PlayerUtils.getPlayerHead
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor

/**
 * View [business]
 */
class ViewBusinessMenu(
    private val previous: MenuProvider? = null,
    private val business: Business
) : BasicMenu() {
    override fun getBuilder(): BaseMenu.Builder = BaseMenu.Builder(3)
        .title(business.name ?: "Unnamed business")
        .previous(previous)

    override fun setup(menu: BaseMenu) {
        /*
        Only display for managers
         */

        // Rename
        menu.getSlot(1, 1).settings = ConditionalSlot.build(
            ItemTemplates.UI.getEdit("Rename ${business.name}"),
            { p -> business.isManager(p) },
            { p, _ ->
                GuiUtil.getAnvilGUIBuilder(
                    "Rename '${business.name}'",
                    business.getIcon(),
                    this@ViewBusinessMenu
                ) { _, input ->
                    // Require an input be provided
                    if (input.isBlank()) AnvilGUI.Response.text(business.name)
                    else {
                        // Rename and save
                        business.name = input
                        business.save()

                        AnvilGUI.Response.close()
                    }
                }.text(business.name).open(p)
            }
        )

        // Change Icon
        menu.getSlot(2, 1).settings = ConditionalSlot.build(
            business.getIcon().setLore("Click to change icon"),
            { p -> business.isManager(p) },
            { p, _ ->
                // Open a material select menu
                MaterialSelectMenu(this@ViewBusinessMenu) { _, material ->
                    // Set and save
                    business.icon = material
                    business.save()
                    true
                }.get().open(p)
            }
        )

        // Remove Icon
        menu.getSlot(3, 1).settings = ConditionalSlot.build(
            ItemTemplates.UI.getXMark("Reset Icon"),
            { p -> business.isManager(p) },
            { p, _ ->
                // Remove and save
                business.icon = null
                business.save()

                get().open(p)
            }
        )

        /*
        Locations
         */

        // View locations
        menu.getSlot(2, 3).apply {
            item = ItemTemplates.getSignshop("See locations")
            openOnClick(BusinessLocationMenu(this@ViewBusinessMenu, business))
        }

        /*
        Ownership
         */

        // Change owner
        // Only displays for the owner
        menu.getSlot(1, 5).settings = ConditionalSlot.build(
            ItemTemplates.UI.getTransfer("Transfer Ownership").setLore(
                "Warning! You will no longer be able to",
                "manage this business unless granted access",
                "by the new owner!",
                "${ChatColor.RED}This cannot be undone"
            ),
            { p -> business.isOwner(p) },
            { p, _ ->
                // Get a player select menu without the current owner
                PlayerSelectMenu(
                    this@ViewBusinessMenu,
                    false,
                    { it.uniqueId != business.owner }
                ) { _, selected ->
                    // Set and save
                    business.owner = selected.uniqueId
                    business.save()
                    true
                }.get().open(p)
            }
        )

        // Current owner
        menu.getSlot(2, 5).item = business.owner!!.getPlayerHead().setLore("Owner")

        /*
        Managers
         */

        // View managers
        menu.getSlot(2, 7).apply {
            item = SkullCreator.createSkull().setName("View Managers")
            openOnClick(BusinessManagerMenu(this@ViewBusinessMenu, business))
        }
    }

}