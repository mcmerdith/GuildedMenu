package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.MenuSize
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
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
class MainMenu(private val admin: Boolean = false) : BaseMenu(
    GuildedMenu.plugin.menuConfig.title,
    MenuSize(5),
    null
) {
    private val config = GuildedMenu.plugin.menuConfig

    init {
        if (admin) {
            // Render the "Admin View" button
            val home = getSlot(config.admin.mainButton.index)
            home.item = ItemTemplates.EXCLAMATION.setName("Admin View")
            GuiUtil.openScreenOnClick(home, AdminMenu(this))
        }

        if (config.vault.enabled
            && IntegrationManager[VaultIntegration::class.java]?.run { ready && hasEconomy() } == true
        ) {
            // BALTOP
            val econ = getSlot(config.vault.index)
            econ.item = ItemTemplates.ECONOMY

            GuiUtil.openScreenSupplierOnClick(econ, { EconomyMenu(this).get() }) { PlayerBalanceMenu(this) }
        }

        val essentials = IntegrationManager[EssentialsIntegration::class.java]

        if (essentials?.ready == true) {
            if (config.tpa.enabled) {
                // TPA
                val tpa = getSlot(config.tpa.index)
                tpa.item = ItemTemplates.TPA

                GuiUtil.openScreenSupplierOnClick(
                    tpa,
                    PlayerSelectMenu(this, true, null, essentials.getTPAExecutor())::get
                )
            }

            if (config.tpaHere.enabled) {
                // TPA HERE
                val tpaHere = getSlot(config.tpaHere.index)
                tpaHere.item = ItemTemplates.TPA_HERE

                GuiUtil.openScreenSupplierOnClick(
                    tpaHere,
                    PlayerSelectMenu(this, true, null, essentials.getTPAHereExecutor())::get
                )
            }
        }

        if (config.signshop.enabled) {
            getSlot(config.signshop.index).apply {
                item = ItemStack(Material.OAK_SIGN).setName("Business Directory")
                GuiUtil.openScreenSupplierOnClick(this, BusinessSelectMenu(this@MainMenu)::get)
            }
        }
    }
}