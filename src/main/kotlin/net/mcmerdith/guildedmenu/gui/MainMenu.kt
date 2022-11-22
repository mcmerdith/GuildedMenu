package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.business.BusinessSelectMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.towny.TownyMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.ifPluginAvailable
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.integration.EssentialsIntegration
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.SignShopIntegration
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import net.mcmerdith.guildedmenu.integration.vault.VaultIntegration
import net.mcmerdith.guildedmenu.util.ItemStackUtils.addLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore

/**
 * Main Menu
 *
 * If [admin] is true a "Switch to Admin View" button will be rendered
 */
class MainMenu(private val admin: Boolean = false) : BasicMenu() {
    private val config = GuildedMenu.plugin.mainMenuConfig

    override fun getBuilder(): BaseMenu.Builder = BaseMenu.Builder(5).title(GuildedMenu.plugin.mainMenuConfig.title)

    override fun setup(menu: BaseMenu) {
        // Plugin info button
        menu.getSlot(config.info.index).apply {
            val itemBase = ItemTemplates.UI.getInfo(config.title).setLore("Developed by: mcmerdith")

            if (admin) {
                // Render the "Admin View" button
                itemBase.addLore("Click to Switch to Admin View")
                openOnClick(AdminMenu(this@MainMenu))
            }

            item = itemBase
        }

        // Economy menu
        if (config.vault.enabled) {
            menu.getSlot(config.vault.index).ifPluginAvailable(VaultIntegration::class.java) {
                item = ItemTemplates.getEconomyIcon()
                openOnClick(EconomyMenu(this@MainMenu), PlayerBalanceMenu(this@MainMenu))
            }
        }

        // Set up essentials stuff
        if (config.tpa.enabled) {
            // TPA
            menu.getSlot(config.tpa.index).ifPluginAvailable(EssentialsIntegration::class.java) {
                item = ItemTemplates.getTPAIcon()
                openOnClick(
                    PlayerSelectMenu(
                        this@MainMenu,
                        true,
                        selectReceiver = IntegrationManager[EssentialsIntegration::class.java]!!.getTPAExecutor()
                    )
                )
            }
        }

        if (config.tpaHere.enabled) {
            // TPA HERE
            menu.getSlot(config.tpaHere.index).ifPluginAvailable(EssentialsIntegration::class.java) {
                item = ItemTemplates.getTPAHereIcon()
                openOnClick(
                    PlayerSelectMenu(
                        this@MainMenu,
                        true,
                        selectReceiver = IntegrationManager[EssentialsIntegration::class.java]!!.getTPAHereExecutor()
                    )
                )
            }
        }

        // Business menu
        if (config.signshop.enabled) {
            menu.getSlot(config.signshop.index).ifPluginAvailable(SignShopIntegration::class.java) {
                item = ItemTemplates.getSignshop()
                openOnClick(BusinessSelectMenu(this@MainMenu))
            }
        }

        // Towny menu
        if (config.towny.enabled) {
            menu.getSlot(config.towny.index).ifPluginAvailable(TownyIntegration::class.java) {
                item = ItemTemplates.getHouse("View Your Town")

                setClickHandler { player, _ ->
                    TownyMenu(this@MainMenu, player).get().open(player)
                }
            }
        }
    }
}