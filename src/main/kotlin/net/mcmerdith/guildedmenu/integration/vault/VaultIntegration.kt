package net.mcmerdith.guildedmenu.integration.vault

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.StaticPlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.integration.Integration
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.ChatUtils.sendSuccessMessage
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.RegisteredServiceProvider
import java.util.stream.Collectors

class VaultIntegration : Integration("Vault") {
    private lateinit var econ: Economy

    override fun onEnable(): Boolean {
        return setupEconomy()
    }

    /**
     * Get the current Vault [Economy]
     *
     * Returns false if no economy is registered
     */
    private fun setupEconomy(): Boolean {
        if (!pluginEnabled) return false
        val rsp: RegisteredServiceProvider<Economy> =
            GuildedMenu.plugin.server.servicesManager.getRegistration(
                Economy::class.java
            ) ?: return false
        econ = rsp.provider
        return true
    }

    /**
     * Get the top balances on this server
     *
     * Sorted high -> low
     */
    fun topBalances(): BalanceTop {
        val players = Bukkit.getOfflinePlayers()
        val balances = mutableListOf<PlayerBalance>()

        for (player in players) {
            balances.add(PlayerBalance(player, econ.getBalance(player)))
        }

        return BalanceTop(
            balances.stream()
                .sorted(Comparator.comparing({ o: PlayerBalance -> o.balance }, Comparator.reverseOrder()))
                .collect(Collectors.toList())
        )
    }

    /**
     * Get [player]'s balance
     */
    fun balance(player: OfflinePlayer) = econ.getBalance(player)

    /**
     * Format [balance]
     */
    fun format(double: Double): String = econ.format(double)

    /**
     * Get [player]'s formatted balance
     */
    fun formattedBalance(player: OfflinePlayer) = format(balance(player))

    /**
     * Get [player]'s head with their formatted balance as the lore
     */
    fun getPlayerBalanceHeadTemplate(player: OfflinePlayer) = StaticPlayerHeadItemTemplate.of(player) { item ->
        item.setLore("Balance: ${formattedBalance(player)}")
    }

    /**
     * Transfer [amount] from [playerToWithdraw] to [playerToDeposit]
     *
     * [callingPlayer] is only used for messaging
     */
    fun transfer(
        playerToWithdraw: OfflinePlayer,
        playerToDeposit: OfflinePlayer,
        amount: Double,
        callingPlayer: Player? = null
    ): Boolean {
        if (!econ.has(playerToWithdraw, amount)) {
            callingPlayer?.sendErrorMessage("Insufficient funds, the transaction is cancelled")
            return false
        }

        // Start the transaction
        val moneyTaken = econ.withdrawPlayer(playerToWithdraw, amount)

        if (!moneyTaken.transactionSuccess()) {
            callingPlayer?.sendErrorMessage(moneyTaken.errorMessage)
            callingPlayer?.sendErrorMessage("The transaction has been cancelled")
            return false
        }

        // Finish the transaction
        val moneyGiven = econ.depositPlayer(playerToDeposit, amount)

        if (!moneyGiven.transactionSuccess()) {
            callingPlayer?.sendErrorMessage(moneyGiven.errorMessage)
            callingPlayer?.sendErrorMessage("The transaction has been cancelled and you have been refunded")
            econ.depositPlayer(playerToWithdraw, amount)
            return false
        }

        callingPlayer?.sendSuccessMessage("Paid ${econ.format(amount)} to ${playerToDeposit.name}")
        return true
    }
}