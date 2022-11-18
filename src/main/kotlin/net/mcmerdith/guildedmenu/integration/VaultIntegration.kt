package net.mcmerdith.guildedmenu.integration

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.components.PlayerBalance
import net.mcmerdith.guildedmenu.util.ChatUtils
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.ChatUtils.sendSuccessMessage
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
        if (!isAvailable) return false
        val rsp: RegisteredServiceProvider<Economy> =
            GuildedMenu.plugin.server.servicesManager.getRegistration(
                Economy::class.java
            ) ?: return false
        econ = rsp.provider
        return true
    }

    fun allBalances(): List<PlayerBalance> {
        val players = Bukkit.getOfflinePlayers()
        val balances: MutableList<PlayerBalance> = ArrayList()

        // DEBUG PAGINATOR (must be repeatable lol. no random)
        val bals = intArrayOf(5000, 7500, 10000, 40000, 80000, 140000, 190000, 200000, 400000, 600000, 600001, 1000000)
        val matt = Bukkit.getOfflinePlayer(UUID.fromString("a8ae1005-73e3-49ba-b94e-bbf5143451bb"))
        for (bal in bals) balances.add(PlayerBalance(matt, bal.toDouble()))
        for (player in players) {
            balances.add(PlayerBalance(player, econ!!.getBalance(player)))
        }
        return balances.stream()
            .sorted(Comparator.comparing({ o: PlayerBalance -> o.balance }, Comparator.reverseOrder()))
            .collect(Collectors.toList())
    }

    fun transfer(
        playerToWithdraw: OfflinePlayer,
        playerToDeposit: OfflinePlayer,
        amount: Double,
        callingPlayer: Player? = null
    ): Boolean {
        if (isAvailable) {
            if (econ?.has(playerToWithdraw, amount) == true) {
                val moneyTaken = econ!!.withdrawPlayer(playerToWithdraw, amount)

                if (moneyTaken.transactionSuccess()) {
                    // Finish the transaction
                    val moneyGiven = econ!!.depositPlayer(playerToDeposit, amount)

                    if (moneyGiven.transactionSuccess()) {
                        callingPlayer?.sendSuccessMessage("Paid ${econ!!.format(amount)} to ${playerToDeposit.name}")
                        return true
                    } else {
                        callingPlayer?.sendErrorMessage(moneyTaken.errorMessage)
                        callingPlayer?.sendErrorMessage("The transaction has been cancelled and you have been refunded")
                    }
                } else {
                    callingPlayer?.sendErrorMessage(moneyTaken.errorMessage)
                    callingPlayer?.sendErrorMessage("The transaction has been cancelled")
                }
            } else callingPlayer?.sendErrorMessage("Insufficient funds, the transaction is cancelled")
        } else callingPlayer?.sendErrorMessage("No economy, the transaction is cancelled (is ${if (econ == null) "an economy plugin" else "Vault"} installed?)")

        return false
    }
}