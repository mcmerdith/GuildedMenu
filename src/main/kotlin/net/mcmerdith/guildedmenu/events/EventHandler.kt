package net.mcmerdith.guildedmenu.events

import me.rockyhawk.commandpanels.api.PanelCommandEvent
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition
import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.VaultIntegration
import net.mcmerdith.guildedmenu.panels.PanelEconomy
import net.mcmerdith.guildedmenu.panels.PanelPlayerSelect
import net.mcmerdith.guildedmenu.panels.builders.CPItemBuilder
import net.mcmerdith.guildedmenu.panels.builders.CPPanelBuilder
import net.mcmerdith.guildedmenu.panels.events.*
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.ChatUtils.sendInfoMessage
import net.mcmerdith.guildedmenu.util.Extensions.asOfflinePlayer
import net.mcmerdith.guildedmenu.util.GMLogger
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class EventHandler : Listener {
    @EventHandler
    fun onPlayerSelect(event: PlayerSelectEvent) {
        val player = event.getPlayer()

        // Open the selection screen
        val gui = PanelPlayerSelect[event.page, event.online, event]

        gui.top?.build()?.open(player, PanelPosition.Top)
        gui.bottom?.build()?.open(player, PanelPosition.Bottom)
    }

    @EventHandler
    fun onBalanceEvent(event: BalanceEvent) {
        val player = event.getPlayer()
        val target = event.targetUUID?.asOfflinePlayer()
        // Load the config
        val balancePanels = PanelEconomy.balance(target ?: player, event.page, target == null)

        if (balancePanels == null) {
            player.sendErrorMessage("Couldn't load BalTop (is Vault installed?)")
            return
        }

        balancePanels.top?.build()?.open(player, PanelPosition.Top)
        balancePanels.middle?.build()?.open(player, PanelPosition.Middle)
        balancePanels.bottom?.build()?.open(player, PanelPosition.Bottom)
    }

    @EventHandler
    fun onPayPlayer(event: PayPlayerEvent) {
        val player = event.getPlayer()
        val target = event.targetUUID?.asOfflinePlayer()

        if (target == null) {
            val gui = PanelPlayerSelect[0, false, event]

            gui.top?.build()?.open(player, PanelPosition.Top)
            gui.bottom?.build()?.open(player, PanelPosition.Bottom)
        } else if (event.amount == null || event.amount!!.isEmpty() || event.amount == "%cp-player-input%") {
            val builder = CPPanelBuilder(null, "amountselector")
                .setRows(1).setTitle("Pay " + target.name)
                .setPanelStatic().setPanelItemsImmovable()

            val setAmountButton = CPItemBuilder()
                .setMaterial(Material.PAPER)
                .setName("Click to input payment amount")
                .addCommand("cpc")
                .addCommand("msg= ${event.playerInputMessage}")
                .setCustomAttribute("player-input", listOf(event.placeholderEventString()))
            builder.setItem(4, setAmountButton)
            builder.build().open(player, PanelPosition.Top)
        } else {
            try {
                val amount = event.amount!!.toDouble()

                IntegrationManager[VaultIntegration::class.java]?.transfer(player, target, amount, player)
            } catch (e: NumberFormatException) {
                player.sendErrorMessage("Amount must be a number")
            }
        }
    }

    @EventHandler
    fun onTPA(event: TPAEvent) {
        val player = event.getPlayer()
        val target = event.targetUUID?.asOfflinePlayer()

        if (target == null) {
            // If no player is selected, open a selection menu
            val gui = PanelPlayerSelect[event]

            gui.top?.build()?.open(player, PanelPosition.Top)
            gui.bottom?.build()?.open(player, PanelPosition.Bottom)
        } else {
            // Make sure the player is online
            val name = target.name

            // Request the TPA
            player.sendInfoMessage("Requesting TPA to $name")
            player.performCommand("tpa $name")
        }
    }

    @EventHandler
    fun onTPAHere(event: TPAHereEvent) {
        val player = event.getPlayer()
        val target = event.targetUUID?.asOfflinePlayer()

        if (target == null) {
            // If no player is selected, open a selection menu
            val gui = PanelPlayerSelect[event]

            gui.top?.build()?.open(player, PanelPosition.Top)
            gui.bottom?.build()?.open(player, PanelPosition.Bottom)
        } else {
            // Make sure the player is online
            val name = target.name

            // Request the TPA
            player.sendInfoMessage("Requesting for $name to TPA")
            player.performCommand("tpahere $name")
        }
    }
}