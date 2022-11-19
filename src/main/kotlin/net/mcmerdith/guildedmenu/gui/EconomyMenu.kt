package net.mcmerdith.guildedmenu.gui

import PlayerBalanceMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.framework.PlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.vault.VaultIntegration
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.Extensions.setName
import net.mcmerdith.guildedmenu.util.GMLogger
import org.ipvp.canvas.Menu

/**
 * Graphical "BalTop" interface
 */
class EconomyMenu(parent: Menu? = null) : PaginatedMenu {
    companion object {
        val LOGGER = GMLogger.getLogger("EconomyMenu")

        val ERROR_ITEM = ItemTemplates.ERROR.setLore("Failed to retrieve balance")
        val MONEY_ITEM = ItemTemplates.MONEYBLOCK.setName()
    }

    private val template: BaseMenu.Builder =
        BaseMenu.Builder(6).title("Top Player Balances").redraw(true).parent(parent)

    private val vault = IntegrationManager[VaultIntegration::class.java]!!
    private val balTop = vault.topBalances()

    /**
     * Get the number of money blocks that represent [balance] as a percentile of all balances
     */
    private fun getBlockCount(balance: Double) = (balTop.percentile(balance) * 8).toInt().coerceIn(0, 8)

    /**
     * A list of [Menu]s with all players (in descending order of their balances) along the left edge
     */
    private val pages: List<Menu> = GuiUtil.getPagination(template, GuiUtil.getRowMask(5, "100000000"))
        .apply {
            for (balance in balTop.balances) addItem(vault.getPlayerBalanceHeadTemplate(balance.player))
        }.build()

    init {
        // Calculate and build the graphs
        for (menu in pages) {
            for (i in 1..5) {
                // Get the left-most slot
                val slot = menu.getSlot(i, 1)
                // Get the player (or continue if the slot does not contain a player head)
                val target = (slot.settings.itemTemplate as? PlayerHeadItemTemplate)?.player ?: continue

                try {
                    // Search for their balance
                    val balance = balTop.balances.find { p -> p.player.uniqueId == target.uniqueId }
                        ?: throw RuntimeException("Could not identify player ${target.name}")

                    // When the head is clicked open a PlayerBalanceMenu for the target player
                    GuiUtil.openScreenOnClick(slot, PlayerBalanceMenu(menu, balance.player))

                    // Place the money blocks
                    for (j in 2..getBlockCount(balance.balance) + 1) menu.getSlot(i, j).item = MONEY_ITEM
                } catch (e: Exception) {
                    LOGGER.error("Failed to load BalTop", e)

                    // Fill the row with error items to indicate there was an issue
                    for (j in 2..9) menu.getSlot(i, j).item = ERROR_ITEM
                }

            }
        }
    }

    override fun get(): Menu {
        return pages.first()
    }
}