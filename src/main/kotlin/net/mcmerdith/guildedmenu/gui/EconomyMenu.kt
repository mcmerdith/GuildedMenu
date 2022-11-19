package net.mcmerdith.guildedmenu.gui

import PlayerBalanceMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.framework.PlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.gui.util.*
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.VaultIntegration
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.Extensions.setName
import net.mcmerdith.guildedmenu.util.GMLogger
import org.ipvp.canvas.Menu

class EconomyMenu(parent: Menu? = null) : PaginatedMenu {
    companion object {
        val LOGGER = GMLogger.getLogger("EconomyMenu")

        val ERROR_ITEM = ItemTemplates.ERROR.setLore("Failed to retrieve balance")
        val MONEY_ITEM = ItemTemplates.MONEYBLOCK.setName()
    }

    private val template: BaseMenu.Builder = BaseMenu.Builder(6).title("Economy").redraw(true).parent(parent)

    private val vault = IntegrationManager[VaultIntegration::class.java]!!
    private val balTop = vault.topBalances()

    private fun getBlockCount(balance: Double) = (balTop.percentile(balance) * 8).toInt().coerceIn(0, 8)

    private val pages: List<Menu> = GuiUtil.getPagination(template, GuiUtil.getRowMask(5, "100000000"))
        .apply {
            for (balance in balTop.balances) addItem(vault.getPlayerBalanceHeadTemplate(balance.player))
        }.build()

    init {
        for (menu in pages) {
            for (i in 1..5) {
                val slot = menu.getSlot(i, 1)
                val target = (slot.settings.itemTemplate as? PlayerHeadItemTemplate)?.player ?: continue

                try {
                    val balance = balTop.balances.find { p -> p.player.uniqueId == target.uniqueId }
                        ?: throw RuntimeException("Could not identify player ${target.name}")

                    GuiUtil.openScreenOnClick(slot, PlayerBalanceMenu(menu, balance.player))

                    for (j in 2..getBlockCount(balance.balance) + 1) menu.getSlot(i, j).item = MONEY_ITEM
                } catch (e: Exception) {
                    LOGGER.warn(e.message ?: "An unknown error occurred")

                    for (j in 2..9) menu.getSlot(i, j).item = ERROR_ITEM
                }

            }
        }
    }

    override fun get(): Menu {
        return pages.first()
    }
}