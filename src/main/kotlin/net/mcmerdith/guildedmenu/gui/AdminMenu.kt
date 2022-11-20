package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuSize
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.Extensions.setName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Admin GUI
 */
class AdminMenu(main: MainMenu) : BaseMenu(
    GuildedMenu.plugin.menuConfig.title + " (Admin)",
    MenuSize(5),
    null
) {
    private val config = GuildedMenu.plugin.menuConfig

    init {
        val home = getSlot(config.admin.mainButton.index)
        home.item = ItemTemplates.EXCLAMATION.setName("Player View")
        GuiUtil.openScreenOnClick(home, main)

        getSlot(3, 5).item = ItemTemplates.EXCLAMATION.setName("Coming Soon!")

        getSlot(1, 5).apply {
            item = ItemStack(Material.STONE).setName("Material Menu DEBUG")
            GuiUtil.openScreenOnClick(this, MaterialSelectMenu(this@AdminMenu) { p, m ->
                BaseMenu("DEBUG", MenuSize(1), this@AdminMenu, false).apply {
                    getSlot(1, 5).item = ItemStack(m)
                }.open(p)
            }.get())
        }
    }
}