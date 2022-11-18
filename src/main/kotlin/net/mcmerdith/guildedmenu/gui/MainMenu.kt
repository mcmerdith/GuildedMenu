package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.integration.EssentialsIntegration
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.VaultIntegration
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import org.bukkit.Bukkit
import org.ipvp.canvas.type.ChestMenu

class MainMenu(val admin: Boolean = false) : BaseMenu(
    "GuildedCraft Menu",
    MenuSize(6),
    null
) {
    init {
        if (IntegrationManager.has(VaultIntegration::class.java)) {
            // BALTOP
            val econ = getSlot(1, 5)
            econ.item = ItemTemplates.ECONOMY

            val economyMenu = EconomyMenu(this)
            GuiUtil.openScreenOnClick(econ, economyMenu, EconomyMenu.PlayerBalanceMenu(economyMenu))
        }

        if (IntegrationManager.has(EssentialsIntegration::class.java)) {
            // TPA
            val tpa = getSlot(2, 3)
            tpa.item = ItemTemplates.TPA

            GuiUtil.openScreenOnClick(tpa, PlayerSelectMenu(this, true) { player, target ->
                if (target.isOnline) {
                    player.performCommand("tpa ${target.name}")
                } else {
                    player.sendErrorMessage("Could not TPA! (is the target online?)")
                }
                close()
            }.get())

            // TPA HERE
            val tpaHere = getSlot(2, 7)
            tpaHere.item = ItemTemplates.TPA_HERE

            GuiUtil.openScreenOnClick(tpaHere, PlayerSelectMenu(this, true) { player, target ->
                if (target.isOnline) {
                    player.performCommand("tpahere ${target.name}")
                } else {
                    player.sendErrorMessage("Could not TPA! (is the target online?)")
                }
                close()
            }.get())
        }
    }
}