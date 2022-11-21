package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuBase
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.integration.EssentialsIntegration
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.vault.VaultIntegration
import net.mcmerdith.guildedmenu.util.Extensions.setName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Main Menu
 *
 * If [admin] is true a "Switch to Admin View" button will be rendered
 */
class MainMenu(private val admin: Boolean = false) : BasicMenu() {
    private val config = GuildedMenu.plugin.menuConfig

    override fun getBuilder(): MenuBase.Builder = MenuBase.Builder(5).title(GuildedMenu.plugin.menuConfig.title)

    override fun setup(menu: MenuBase) {
        menu.apply {
            if (admin) {
                // Render the "Admin View" button
                getSlot(config.admin.mainButton.index).apply {
                    item = ItemTemplates.EXCLAMATION.setName("Admin View")
                    openOnClick(AdminMenu(this@MainMenu))
                }
            }

            if (config.vault.enabled
                && IntegrationManager[VaultIntegration::class.java]?.run { ready && hasEconomy() } == true
            ) {
                // BALTOP
                getSlot(config.vault.index).apply {
                    item = ItemTemplates.ECONOMY
                    openOnClick(EconomyMenu(this@MainMenu), PlayerBalanceMenu(this@MainMenu))
                }
            }

            val essentials = IntegrationManager[EssentialsIntegration::class.java]

            if (essentials?.ready == true) {
                if (config.tpa.enabled) {
                    // TPA
                    getSlot(config.tpa.index).apply {
                        item = ItemTemplates.TPA
                        openOnClick(
                            PlayerSelectMenu(this@MainMenu, true, null, essentials.getTPAExecutor())
                        )
                    }
                }

                if (config.tpaHere.enabled) {
                    // TPA HERE
                    getSlot(config.tpaHere.index).apply {
                        item = ItemTemplates.TPA_HERE
                        openOnClick(
                            PlayerSelectMenu(this@MainMenu, true, null, essentials.getTPAHereExecutor())
                        )
                    }
                }
            }

            if (config.signshop.enabled) {
                getSlot(config.signshop.index).apply {
                    item = ItemStack(Material.OAK_SIGN).setName("Business Directory")
                    openOnClick(BusinessSelectMenu(this@MainMenu))
                }
            }
        }
    }
}