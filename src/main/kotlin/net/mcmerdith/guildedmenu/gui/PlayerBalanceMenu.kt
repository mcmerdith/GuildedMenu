import net.mcmerdith.guildedmenu.gui.PlayerSelectMenu
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.gui.framework.MenuSize
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.vault.VaultIntegration
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.Extensions.setName
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.ipvp.canvas.Menu

/**
 * Player Balance Menu
 *
 * If [target] is not provided the menu will render for the current viewer
 */
class PlayerBalanceMenu(parent: Menu? = null, private val target: OfflinePlayer? = null) : BaseMenu(
    "Player Balance",
    MenuSize(3),
    parent
) {
    private val vault = IntegrationManager[VaultIntegration::class.java]!!

    init {
        if (target == null) {
            // Render the menu with the viewer as the target

            // Render the player head 1 slot left of center
            getSlot(2, 4).setItemTemplate { viewer ->
                vault.getPlayerBalanceHeadTemplate(viewer).item
            }

            // Generic "Send Money" button 1 slot right of center
            getSlot(2, 6).apply {
                item = ItemTemplates.REGISTER_ORANGE.setName("Send Money")
                GuiUtil.openScreenOnClick(
                    this,
                    PlayerSelectMenu(this@PlayerBalanceMenu, false) { callingPlayer, selectedPlayer ->
                        initTransaction(callingPlayer, selectedPlayer)
                    }.get()
                )
            }
        } else {
            // Render the player head in the center of the GUI
            getSlot(2, 5).setItemTemplate(vault.getPlayerBalanceHeadTemplate(target))

            // Request button on the left
            getSlot(2, 3).apply {
                item = ItemTemplates.REGISTER_GREEN.setName("Request from ${target.name}").setLore("Coming Soon!")
            }

            // Send button on the right
            getSlot(2, 7).apply {
                item = ItemTemplates.REGISTER_ORANGE.setName("Pay ${target.name}")

                setClickHandler { player, _ ->
                    initTransaction(player, target)
                }
            }
        }
    }

    /**
     * Prompt [sender] for an amount to send to [receiver]
     */
    private fun initTransaction(sender: Player, receiver: OfflinePlayer) {
        GuiUtil.getAnvilGUIBuilder(
            "Pay ${receiver.name}",
            ItemTemplates.MONEYBLOCK.setLore("Your Balance: ${vault.formattedBalance(sender)}"),
            this@PlayerBalanceMenu
        ) { _, input ->
            val amount = input.toDoubleOrNull()

            if (amount == null) AnvilGUI.Response.text("Numbers only!")
            else if (vault.transfer(sender, receiver, amount, sender)) AnvilGUI.Response.close()
            else AnvilGUI.Response.text("Insufficient funds!")
        }.text("Enter Amount").open(sender)
    }
}