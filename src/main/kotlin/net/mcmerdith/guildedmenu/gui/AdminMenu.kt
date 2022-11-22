package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates

/**
 * Admin GUI
 */
class AdminMenu(private val main: MainMenu) : BasicMenu() {
    private val config = GuildedMenu.plugin.menuConfig

    override fun getBuilder() =
        BaseMenu.Builder(5).title(GuildedMenu.plugin.menuConfig.title + " (Admin)").previous(main)

    override fun setup(menu: BaseMenu) {
        // Switch view
        menu.getSlot(config.admin.mainButton.index).apply {
            item = ItemTemplates.UI.getExclamation("Player View")
            openOnClick(main)
        }

        // Stuff goes here
        menu.getSlot(3, 5).item = ItemTemplates.UI.getExclamation("Coming Soon!")

    }
}