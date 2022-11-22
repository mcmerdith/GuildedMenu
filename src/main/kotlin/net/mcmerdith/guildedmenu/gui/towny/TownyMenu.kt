package net.mcmerdith.guildedmenu.gui.towny

import com.palmergames.bukkit.towny.TownyFormatter
import com.palmergames.bukkit.towny.TownySettings
import com.palmergames.bukkit.towny.`object`.Town
import com.palmergames.bukkit.towny.`object`.TownyPermission.ActionType
import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.framework.ConditionalSlot
import net.mcmerdith.guildedmenu.gui.framework.PlayerHeadItemTemplate
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.GuiUtil.openOnClick
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import net.mcmerdith.guildedmenu.util.ItemStackUtils.addLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.PlayerUtils.asTownyResident
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.slot.Slot

class TownyMenu(
    private val previous: MenuProvider?,
    private val player: Player,
    private val targetTown: Town? = null
) : BasicMenu() {
    private val towny by lazy { IntegrationManager[TownyIntegration::class.java]!! }
    private val resident by lazy { player.asTownyResident() }

    private fun getTown() = targetTown ?: resident?.townOrNull

    override fun getBuilder() = BaseMenu.Builder(5)
        .title("${player.displayName} (${targetTown?.name ?: "nomad"})")
        .previous(previous)

    override fun setup(menu: BaseMenu) {
        val town = getTown() ?: run {
            menu.getSlot(3, 5).apply {
                item = ItemTemplates.UI.getExclamation("You are not a member of a town")
                    .setLore("Click to browse public towns")
                setClickHandler { player, click ->
                    // TODO Add town browser
                }
            }
            return
        }

        menu.getSlot(1, 2).setItemTemplate(PlayerHeadItemTemplate { item, _ ->
            item.setLore(town.name)
        })

        menu.getSlot(1, 5).apply {
            setItemTemplate { player ->
                val item = ItemStack(Material.OAK_SIGN).setName("Town Board").setLore(town.board)

                if (town.isMayor(player.asTownyResident())) // TODO better conditional
                    item.addLore("Click to update message")

                item
            }
            setClickHandler(ConditionalSlot.ConditionalClickHandler(
                { player ->
                    town.isMayor(player.asTownyResident())
                }, { player, _ ->
                    GuiUtil.getAnvilGUIBuilder(
                        "New board message",
                        ItemStack(Material.OAK_SIGN).setLore(town.board),
                        this@TownyMenu
                    ) { p, input ->
                        towny.townSetBoard(p, town, input)
                        AnvilGUI.Response.close()
                    }.text(town.board).open(player)
                }
            ))
        }

        menu.getSlot(3, 2).apply {
            setItemTemplate { player ->
                ItemTemplates.getMoneyBlock("Town Bank")
                    .setLore(
                        "Value: ${town.account.holdingFormattedBalance}",
                        "Upkeep: ${if (town.hasUpkeep()) TownySettings.getTownUpkeepCost(town) else "None"}",
                        "Tax: ${town.taxes}${if (town.isTaxPercentage) "%" else ""}",
                    ).apply {
                        if (town.hasResident(player)) addLore("Click to deposit funds")
                        if (town.isMayor(player.asTownyResident())) addLore("${ChatColor.RED}Right click to withdraw")
                    }
            }
            setClickHandler { player, click ->
                val resident = player.asTownyResident() ?: return@setClickHandler
                val withdraw = click.clickType.isRightClick && town.isMayor(resident)

                GuiUtil.getAnvilGUIBuilder(
                    "${if (withdraw) "Withdraw from" else "Deposit to"} ${town.name}",
                    ItemTemplates.getMoneyBlock().setLore("Your Balance: ${resident.account.holdingFormattedBalance}"),
                    this@TownyMenu
                ) { _, input ->
                    val amount = input.toIntOrNull()

                    if (amount == null) AnvilGUI.Response.text("Whole numbers only!")
                    else {
                        if (withdraw) towny.townWithdraw(player, resident, town, amount)
                        else if (town.hasResident(player)) towny.townDeposit(player, resident, town, amount)

                        AnvilGUI.Response.close()
                    }
                }.text("Enter Amount").open(player)
            }
        }

        menu.getSlot(3, 5).item =
            ItemTemplates.UI.getInfo("Founded ${TownyFormatter.registeredFormat.format(town.registered)}")

        menu.getSlot(5, 2).apply {
            item = ItemStack(Material.GRASS_BLOCK).setName("View Plots")
            // TODO plot viewer
        }

        menu.getSlot(5, 5).apply {
            item = SkullCreator.createSkull().setName("View Residents")
            openOnClick(TownResidentViewer(this@TownyMenu, town))
        }

        buildPermissionsItem(menu.getSlot(1, 8), ActionType.BUILD, town)
        buildPermissionsItem(menu.getSlot(2, 8), ActionType.DESTROY, town)
        buildPermissionsItem(menu.getSlot(3, 8), ActionType.SWITCH, town)
        buildPermissionsItem(menu.getSlot(4, 8), ActionType.ITEM_USE, town)

        menu.getSlot(5, 8).apply {
            item = ItemTemplates.UI.getRefresh("Settings")
            openOnClick(TownSettingsMenu(this@TownyMenu, town))
        }
    }

    private fun buildPermissionsItem(slot: Slot, action: ActionType, town: Town) {
        slot.item = towny.getPermissionsItem(action, town)
        slot.openOnClick(TownPermissionsMenu(this@TownyMenu, town, action))
    }
}