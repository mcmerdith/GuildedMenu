package net.mcmerdith.guildedmenu.integration.vault

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.gui.framework.PlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.integration.Integration
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.ChatUtils.sendSuccessMessage
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.RegisteredServiceProvider
import java.util.*
import java.util.stream.Collectors

class VaultIntegration : Integration("Vault") {
    var econ: Economy? = null
        private set

    override fun onEnable(): Boolean {
        return setupEconomy()
    }

    private fun setupEconomy(): Boolean {
        if (!pluginEnabled) return false
        val rsp: RegisteredServiceProvider<Economy> =
            GuildedMenu.plugin.server.servicesManager.getRegistration(
                Economy::class.java
            ) ?: return false
        econ = rsp.provider
        return true
    }

    fun hasEconomy() = econ != null

    fun topBalances(): BalanceTop {
        val players = Bukkit.getOfflinePlayers()
        val balances: MutableList<PlayerBalance> = ArrayList()

        // DEBUG PAGINATOR (must be repeatable lol. no random)
        val bals = intArrayOf(5000, 7500, 10000, 40000, 80000, 140000, 190000, 200000, 400000, 600000, 600001, 1000000)
//        for (bal in bals) balances.add(PlayerBalance(Globals.DEBUG_PLAYER, bal.toDouble()))
        for (bal in bals) balances.add(PlayerBalance(Bukkit.getOfflinePlayer(UUID.randomUUID()), bal.toDouble()))

        for (player in players) {
            balances.add(PlayerBalance(player, econ!!.getBalance(player)))
        }

        return BalanceTop(
            balances.stream()
                .sorted(Comparator.comparing({ o: PlayerBalance -> o.balance }, Comparator.reverseOrder()))
                .collect(Collectors.toList())
        )
    }

    fun balance(player: OfflinePlayer) = econ!!.getBalance(player)

    fun format(double: Double) = econ!!.format(double)

    fun formattedBalance(player: OfflinePlayer): String {
        val balance = balance(player)
        return format(balance) ?: balance.toString()
    }

    fun getPlayerBalanceHeadTemplate(player: OfflinePlayer) = PlayerHeadItemTemplate.of(player).apply {
        rawItem.setLore("Balance: ${formattedBalance(player)}")
    }

    fun transfer(
        playerToWithdraw: OfflinePlayer,
        playerToDeposit: OfflinePlayer,
        amount: Double,
        callingPlayer: Player? = null
    ): Boolean {
        if (!ready) {
            callingPlayer?.sendErrorMessage("No economy, the transaction is cancelled (is ${if (econ == null) "an economy plugin" else "Vault"} installed?)")
            return false
        }

        if (econ?.has(playerToWithdraw, amount) != true) {
            callingPlayer?.sendErrorMessage("Insufficient funds, the transaction is cancelled")
            return false
        }

        // Start the transaction
        val moneyTaken = econ!!.withdrawPlayer(playerToWithdraw, amount)

        if (!moneyTaken.transactionSuccess()) {
            callingPlayer?.sendErrorMessage(moneyTaken.errorMessage)
            callingPlayer?.sendErrorMessage("The transaction has been cancelled")
            return false
        }

        // Finish the transaction
        val moneyGiven = econ!!.depositPlayer(playerToDeposit, amount)

        if (!moneyGiven.transactionSuccess()) {
            callingPlayer?.sendErrorMessage(moneyGiven.errorMessage)
            callingPlayer?.sendErrorMessage("The transaction has been cancelled and you have been refunded")
            econ!!.depositPlayer(playerToWithdraw, amount)
            return false
        }

        callingPlayer?.sendSuccessMessage("Paid ${econ!!.format(amount)} to ${playerToDeposit.name}")
        return true
    }
}