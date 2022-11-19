import net.mcmerdith.guildedmenu.gui.util.BaseMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.gui.util.MenuSize
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.VaultIntegration
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.Extensions.setName
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.OfflinePlayer
import org.ipvp.canvas.Menu

class PlayerBalanceMenu(parent: Menu? = null, val target: OfflinePlayer? = null) : BaseMenu(
    "Player Balance",
    MenuSize(3),
    parent
) {
    private val vault = IntegrationManager[VaultIntegration::class.java]!!

    init {
        getSlot(2, 5).setItemTemplate { v -> vault.getPlayerBalanceHeadTemplate(target ?: v).item }

        if (target != null) {
            getSlot(2, 3).apply {
                item = ItemTemplates.REGISTER_GREEN.setName("Request from ${target.name}").setLore("Coming Soon!")
            }

            getSlot(2, 7).apply {
                item = ItemTemplates.REGISTER_ORANGE.setName("Pay ${target.name}")

                setClickHandler { player, _ ->
                    GuiUtil.getAnvilGUIBuilder(
                        "Pay ${target.name}",
                        ItemTemplates.MONEYBLOCK.setLore("Balance: ${vault.formattedBalance(player)}"),
                        this@PlayerBalanceMenu
                    ) { _, input ->
                        val amount = input.toDoubleOrNull()

                        if (amount == null) AnvilGUI.Response.text("Numbers only!")
                        else if (vault.transfer(player, target, amount, player)) AnvilGUI.Response.close()
                        else AnvilGUI.Response.text("Insufficient funds!")
                    }.text("Enter Amount").open(player)
                }
            }
        }
    }
}