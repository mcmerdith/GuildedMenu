package net.mcmerdith.guildedmenu.gui.towny

import com.palmergames.bukkit.towny.TownyFormatter
import com.palmergames.bukkit.towny.TownySettings
import com.palmergames.bukkit.towny.`object`.Town
import com.palmergames.bukkit.towny.`object`.TownyPermission.ActionType
import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.gui.framework.ConditionalClickHandler
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
import net.mcmerdith.guildedmenu.util.PlayerUtils.canEdit
import net.mcmerdith.guildedmenu.util.PlayerUtils.getHeadItem
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.slot.Slot

/**
 * View [targetTown] as [player]
 *
 * If [targetTown] is not provided [player]'s hometown will be used
 */
class TownyMenu(
    private val previous: MenuProvider?,
    private val player: Player,
    private val targetTown: Town? = null
) : BasicMenu() {
    private val towny by lazy { IntegrationManager[TownyIntegration::class.java]!! }
    private val resident by lazy { player.asTownyResident() }

    private val town
        get() = targetTown ?: resident?.townOrNull

    private fun getTitle(): String {
        val t = town
        val pName = player.displayName

        return if (t == null) "$pName (nomad)"
        else if (t.hasResident(player)) "$pName (${t.name})"
        else t.name
    }

    override fun getBuilder() = BaseMenu.Builder(5)
        .title(getTitle())
        .previous(previous)

    override fun setup(menu: BaseMenu) {
        /*
        Sidebar
         */

        menu.getSlot(2, 2).apply {
            item = ItemTemplates.getHouse("View All Towns")
            openOnClick(TownBrowserMenu(this@TownyMenu) { p, town ->
                TownyMenu(this@TownyMenu, p, town).get().open(p)
                false
            })
        }

        // Validate that we have a town to display before continuing
        val town = this.town ?: run {
            // Player does not have a town and one was not selected
            menu.getSlot(3, 5).apply {
                item = ItemTemplates.UI.getExclamation("You are not a member of a town")
                    .setLore("Click to browse open towns")
                openOnClick(TownBrowserMenu(this@TownyMenu, { town -> town.isOpen }) { p, town ->
                    towny.townJoin(p, town)
                    true
                })
            }
            return
        }

        if (town.hasResident(player)) {
            menu.getSlot(4, 2).apply {
                item = ItemTemplates.UI.getXMark("Leave Town")
                setClickHandler { player, _ ->
                    towny.townInitiateLeave(player, this@TownyMenu)
                }
            }
        }

        /*
        UI
         */

        // Current player
        menu.getSlot(1, 4).apply {
            setItemTemplate(PlayerHeadItemTemplate { item, p ->
                if (p != null && town.hasResident(p)) item.setLore(town.name)
                else item.setLore("Click to View Your Town")
            })

            setClickHandler(
                ConditionalClickHandler(
                    { player -> !town.hasResident(player) },
                    { player, _ ->
                        TownyMenu(this@TownyMenu, player).get().open(player)
                    }
                )
            )
        }

        // Town board
        menu.getSlot(1, 6).apply {
            setItemTemplate { player ->
                val item = ItemStack(Material.OAK_SIGN).setName("Town Board").setLore(town.board)

                // Only the mayor can update the board
                if (player.canEdit(town)) item.addLore("${ChatColor.GOLD}Click to update message")

                item
            }
            setClickHandler(
                ConditionalClickHandler(
                    { player -> player.canEdit(town) },
                { player, _ ->
                    // Get text input
                    GuiUtil.getAnvilGUIBuilder(
                        "New board message",
                        ItemStack(Material.OAK_SIGN).setLore(town.board),
                        this@TownyMenu
                    ) { p, input ->
                        // Update board
                        towny.townSetBoard(p, town, input)
                        AnvilGUI.Response.close()
                    }.text(town.board).open(player)
                }
            ))
        }

        // Town bank
        menu.getSlot(3, 4).apply {
            setItemTemplate { player ->
                ItemTemplates.getMoneyBlock("Town Bank")
                    .setLore(
                        "Value: ${town.account.holdingFormattedBalance}",
                        "Upkeep: ${if (town.hasUpkeep()) TownySettings.getTownUpkeepCost(town) else "None"}",
                        "Tax: ${town.taxes}${if (town.isTaxPercentage) "%" else ""}",
                    ).apply {
                        // Residents can deposit
                        if (town.hasResident(player)) addLore("Click to deposit funds")
                        // Mayor can withdraw
                        if (town.isMayor(player.asTownyResident())) addLore("${ChatColor.RED}Right click to withdraw")
                    }
            }
            setClickHandler(
                ConditionalClickHandler(
                    { player -> town.hasResident(player) },
                    { player, click ->
                        // Withdraw/Deposit
                        val resident = player.asTownyResident()!!

                        // Only mayor can withdraw
                        val withdraw = click.clickType.isRightClick && town.isMayor(resident)

                        // Get amount input
                        GuiUtil.getAnvilGUIBuilder(
                            "${if (withdraw) "Withdraw from" else "Deposit to"} ${town.name}",
                            ItemTemplates.getMoneyBlock()
                                .setLore("Your Balance: ${resident.account.holdingFormattedBalance}"),
                            this@TownyMenu
                        ) { _, input ->
                            // Must have an integer amount
                            val amount = input.toIntOrNull()

                            if (amount == null) AnvilGUI.Response.text("Whole numbers only!")
                            else {
                                // Withdraw if set
                                if (withdraw) towny.townWithdraw(player, resident, town, amount)
                                // Otherwise make sure player is resident
                                else towny.townDeposit(player, resident, town, amount)

                                AnvilGUI.Response.close()
                            }
                        }.text("Enter Amount").open(player)
                    }
                )
            )
        }

        // Founding date
        menu.getSlot(3, 6).apply {
            setItemTemplate { player ->
                val item = (town.mayor.player?.getHeadItem()
                    ?: SkullCreator.createSkull()).setName("Mayor: ${town.mayor.name}")

                item.setLore("Founded ${TownyFormatter.registeredFormat.format(town.registered)}")
                if (player.canEdit(town)) item.addLore("${ChatColor.RED}Transfer Ownership")

                item
            }
            setClickHandler(
                ConditionalClickHandler(
                    { player -> player.canEdit(town) },
                    { player, _ ->
                        TownResidentViewer(this@TownyMenu, town) { p, resident ->
                            towny.townSetMayor(p, resident, town)
                            true
                        }.get().open(player)
                    }
                )
            )
        }

        // View town plots
        menu.getSlot(5, 4).apply {
            item = ItemStack(Material.GRASS_BLOCK).setName("View Plots")
            // TODO plot viewer
        }

        // View residents
        menu.getSlot(5, 6).apply {
            item = SkullCreator.createSkull().setName("View Residents")
            openOnClick(TownResidentViewer(this@TownyMenu, town))
        }

        // Display current permissions
        buildPermissionsItem(menu.getSlot(1, 8), ActionType.BUILD, town)
        buildPermissionsItem(menu.getSlot(2, 8), ActionType.DESTROY, town)
        buildPermissionsItem(menu.getSlot(3, 8), ActionType.SWITCH, town)
        buildPermissionsItem(menu.getSlot(4, 8), ActionType.ITEM_USE, town)

        // Open town settings menu
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