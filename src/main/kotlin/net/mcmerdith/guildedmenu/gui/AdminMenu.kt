package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.mythic.MMOItemSelectMenu
import net.mcmerdith.guildedmenu.gui.mythic.MythicMobSelectMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.ifPluginAvailable
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.integration.MMOItemsIntegration
import net.mcmerdith.guildedmenu.integration.MythicMobsIntegration
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Admin GUI
 */
class AdminMenu(private val main: MainMenu) : BasicMenu() {
    private val config = GuildedMenu.plugin.mainMenuConfig

    override fun getBuilder() =
        BaseMenu.Builder(5).title(GuildedMenu.plugin.mainMenuConfig.title + " (Admin)").previous(main)

    override fun setup(menu: BaseMenu) {
        // Switch view
        menu.getSlot(config.admin.mainButton.index).apply {
            item = ItemTemplates.UI.getInfo("Switch to Player View")
            openOnClick(main)
        }

        // Stuff goes here
        menu.getSlot(3, 5).item = ItemTemplates.UI.getExclamation("Coming Soon!")

        if (config.admin.mythic.enabled) {
            menu.getSlot(config.admin.mythic.index).ifPluginAvailable(MythicMobsIntegration::class.java) {
                item = ItemStack(Material.STONE).setName("Mythic Mob Spawner")
                openOnClick(MythicMobSelectMenu(this@AdminMenu))
            }
        }

        if (config.admin.mmoitems.enabled) {
            menu.getSlot(config.admin.mmoitems.index).ifPluginAvailable(MMOItemsIntegration::class.java) {
                item = ItemStack(Material.STONE).setName("MMO Item Spawner")
                openOnClick(MMOItemSelectMenu(this@AdminMenu))
            }
        }
    }
}