package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.gui.util.*
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.VaultIntegration
import net.mcmerdith.guildedmenu.util.Extensions.addLore
import net.mcmerdith.guildedmenu.util.Extensions.name
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.GMLogger
import net.mcmerdith.guildedmenu.util.Globals
import org.ipvp.canvas.Menu

class EconomyMenu(parent: Menu? = null) {
    companion object {
        val LOGGER = GMLogger.getLogger("EconomyMenu")
    }

    class PlayerBalanceMenu(parent: Menu? = null) : BaseMenu(
        "Player Balance",
        MenuSize(3),
        parent
    )

    val TEMPLATE = BaseMenu.Builder(6).title("Economy").redraw(true).parent(parent)

    private val vault = IntegrationManager[VaultIntegration::class.java]!!
    private val balances = vault.allBalances()

    private val offset = balances.lastOrNull()?.balance ?: 0.0
    private val offsetMax = balances.firstOrNull()?.balance?.minus(offset) ?: 0.0

    private fun getBlockCount(balance: Double) = (((balance - offset) / offsetMax) * 8).toInt().coerceIn(0, 8)

    private val pages: List<Menu> = GuiUtil.getPagination(TEMPLATE, GuiUtil.getRowMask(5, "100000000"))
        .apply {
            for (balance in balances) {
                val template = PlayerHeadItemTemplate.of(balance.player)
                template.rawItem.setLore(vault.econ?.format(balance.balance) ?: balance.balance.toString())
                addItem(template)
            }
        }.build()

    init {
        for (menu in pages) {
            for (i in 1..5) {
                val player = (menu.getSlot(i, 1).settings.itemTemplate as? PlayerHeadItemTemplate)?.player ?: continue

                try {
                    val balance = balances.find { p -> p.player.uniqueId == player.uniqueId }
                        ?: throw RuntimeException("Could not identify player ${player.name}")

                    for (j in 2..getBlockCount(balance.balance) + 1) {
                        menu.getSlot(i, j).item = ItemTemplates.MONEYBLOCK.name()
                    }
                } catch (e: Exception) {
                    LOGGER.warn(e.message ?: "An unknown error occurred")

                    val error = ItemTemplates.ERROR.setLore("Failed to retrieve balance")

                    for (j in 2..9) {
                        menu.getSlot(i, j).item = error
                    }
                }

            }
        }
    }

    fun get(): Menu {
        return pages.first()
    }
}