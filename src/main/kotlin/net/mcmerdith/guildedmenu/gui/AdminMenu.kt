package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuSize
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.Extensions.setName

/**
 * Admin GUI
 */
class AdminMenu(main: MainMenu) : BaseMenu(
    GuildedMenu.plugin.menuConfig.title + " (Admin)",
    MenuSize(3),
    null
) {
    private val config = GuildedMenu.plugin.menuConfig

    init {
        val home = getSlot(config.admin.mainButton.index)
        home.item = ItemTemplates.EXCLAMATION.setName("Player View")
        GuiUtil.openScreenOnClick(home, main)

        getSlot(2, 5).item = ItemTemplates.EXCLAMATION.setName("Coming Soon!")
    }
}