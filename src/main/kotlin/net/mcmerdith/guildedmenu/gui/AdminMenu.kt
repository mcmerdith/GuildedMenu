package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuBase
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.Extensions.setName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Admin GUI
 */
class AdminMenu(private val main: MainMenu) : BasicMenu() {
    private val config = GuildedMenu.plugin.menuConfig

    override fun getBuilder() =
        MenuBase.Builder(5).title(GuildedMenu.plugin.menuConfig.title + " (Admin)").previous(main)

    override fun setup(menu: MenuBase) {
        menu.apply {
            getSlot(config.admin.mainButton.index).apply {
                item = ItemTemplates.EXCLAMATION.setName("Player View")
                openOnClick(main)
            }

            getSlot(3, 5).item = ItemTemplates.EXCLAMATION.setName("Coming Soon!")

            getSlot(1, 5).apply {
                item = ItemStack(Material.STONE).setName("Material Menu DEBUG")
                openOnClick(MaterialSelectMenu(this@AdminMenu) { p, m ->
                    MenuBase.Builder(1).title("DEBUG").previous(this@AdminMenu).build().apply {
                        getSlot(1, 5).item = ItemStack(m)
                    }.open(p)
                    false
                })
            }
        }
    }
}