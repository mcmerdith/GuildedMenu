package net.mcmerdith.guildedmenu.panels

import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.VaultIntegration
import net.mcmerdith.guildedmenu.panels.builders.CPItemBuilder
import net.mcmerdith.guildedmenu.panels.builders.CPPanelBuilder
import net.mcmerdith.guildedmenu.panels.gui.PageData
import org.bukkit.OfflinePlayer
import kotlin.math.ceil

/**
 * GUI to view balance of Vault account
 */
object PanelEconomy {
    /**
     * Get a config file containing all balance panels
     *
     * @param player     The targeted player
     * @param balTopPage What page of baltop to view
     * @param isSelf     If the player is viewing their own balance
     * @return A CommandPanels config
     */
    fun balance(player: OfflinePlayer, page: Int, isSelf: Boolean): ComboPanel? {
        val vaultIntegration = IntegrationManager[VaultIntegration::class.java]
        vaultIntegration?.run {
            if (!isAvailable) return null;

            // Set up BalTop
            val baltopBuilder = CPPanelBuilder(null, "baltop")
                .setTitle("Top Player Balances").setRows(6)
                .setPanelStatic().setPanelItemsImmovable()

            val balances = allBalances()

            val pageData = PageData(balances, page, 6)

            if (balances.isNotEmpty()) {
                // Calculate the balance range
                val topBalance = balances[0].balance
                val minBalance = balances[balances.size - 1].balance
                val balanceRange = topBalance - minBalance

                // Add the current balances
                for (i in 0 until pageData.pageCount) {
                    val playerBalance = balances[pageData.startIndex + i]
                    val name = playerBalance.player.name

                    // First index on this players line
                    val lineStart = i * 9

                    val playerHeadItem = CPItemBuilder()
                        .setMaterial("cps= $name")
                        .setName(name)
                        .addLore(playerBalance.balance.toString())
                        .addCommand(BalanceEvent(playerBalance.player.uniqueId, pageData.page).toEventString())

                    baltopBuilder.setItem(lineStart, playerHeadItem)

                    val balanceMultiplier = (playerBalance.balance - minBalance) / balanceRange
                    val blockCount = ceil(balanceMultiplier * 8).toInt().coerceAtMost(8)
                    val firstBlockItem = CPItemBuilder.MONEY_BLOCK
                        .setName("&f")
                        .setCustomAttribute(
                            "duplicate",
                            "${lineStart + 1}-${lineStart + blockCount + 1}"
                        )
                    baltopBuilder.setItem(lineStart + 1, firstBlockItem)
                }
            }

            // Individual player balance
            val playerBalanceBuilder = CPPanelBuilder(null, "playerBalance")
                .setTitle("Player Balance").setRows(3)
                .setPanelStatic().setPanelItemsImmovable()

            val balance = econ!!.getBalance(player)
            val playerHeadItem = CPItemBuilder()
                .setMaterial("cps= " + player.name)
                .setName(player.name)
                .addLore(balance.toString())
                .addCommand(BalanceEvent(player.uniqueId, pageData.page).toEventString())
            playerBalanceBuilder.setItem(0, playerHeadItem)

            if (isSelf) {
                // Show their balance controls
                val payPlayer = CPItemBuilder.MONEY_BLOCK
                    .setName("Pay a player")
                    .addLore("/pay")
                    .addCommand("cpc")
                    .addCommand(PayPlayerEvent(null, null).placeholderEventString())
                playerBalanceBuilder.setItem(13, payPlayer)
            } else {
                // Show the "pay this player button"
                val payPlayer = CPItemBuilder.MONEY_BLOCK
                    .setName("Pay " + player.name)
                    .addLore("/pay " + player.name)
                    .addCommand("cpc")
                    .addCommand("msg= Enter the amount to pay " + player.name)
                    .setCustomAttribute(
                        "player-input",
                        listOf(PayPlayerEvent(player.uniqueId, null).placeholderEventString())
                    )
                playerBalanceBuilder.setItem(13, payPlayer)
            }

            // Generate controls
            val controlsBuilder = CPPanelBuilder.getControlsPanel(pageData,
                BalanceEvent(player.uniqueId),
            )

            return ComboPanel(baltopBuilder, playerBalanceBuilder, controlsBuilder)

        } ?: return null
    }
}