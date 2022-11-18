package net.mcmerdith.guildedmenu.gui

import net.mcmerdith.guildedmenu.integration.EssentialsIntegration
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.VaultIntegration
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.ipvp.canvas.Menu
import org.ipvp.canvas.slot.Slot
import org.ipvp.canvas.type.ChestMenu
import java.util.*
import java.util.function.BiConsumer

object GuiTemplates {
    private val MAIN_MENU
        get() = ChestMenu.builder(3).title("GuildedCraft Menu").build().apply {
            if (IntegrationManager.has(VaultIntegration::class.java)) {
                // BALTOP
                val econ = getSlot(1, 5)
                econ.item = ItemTemplates.ECONOMY

                val economyMenu = getEconomyMenu(this)
                openScreenOnClick(econ, economyMenu, getPlayerBalanceMenu(economyMenu))
            }

            if (IntegrationManager.has(EssentialsIntegration::class.java)) {
                // TPA
                val tpa = getSlot(2, 3)
                tpa.item = ItemTemplates.TPA

                openScreenOnClick(tpa, getPlayerSelectMenu { player, uuid ->
                    Bukkit.getPlayer(uuid)?.let { player.performCommand("tpa ${it.name}") }
                        ?: player.sendErrorMessage("Could not TPA! (is the target online?)")
                })

                // TPA HERE
                val tpaHere = getSlot(2, 7)
                tpaHere.item = ItemTemplates.TPA_HERE

                openScreenOnClick(tpaHere, getPlayerSelectMenu { player, uuid ->
                    Bukkit.getPlayer(uuid)?.let { player.performCommand("tpahere ${it.name}") }
                        ?: player.sendErrorMessage("Could not TPA! (is the target online?)")
                })
            }
        }

    fun getMainMenu(previous: Menu? = null) = getMenu(MAIN_MENU, previous)

    private val ECONOMY
        get() = ChestMenu.builder(6).title("Top Player Balances").build().apply {

        }

    fun getEconomyMenu(previous: Menu? = null) = getMenu(ECONOMY, previous)

    private val PLAYER_BALANCE
        get() = ChestMenu.builder(3).title("Player Balance").build().apply {

        }

    fun getPlayerBalanceMenu(previous: Menu? = null) = getMenu(PLAYER_BALANCE, previous)

    private val P_SELECT_TEMPLATE
        get() = ChestMenu.builder(6).title("Player Select").build()

    fun getPlayerSelectMenu(previous: Menu? = null, callback: (Player, UUID) -> Unit) =
        getMenu(P_SELECT_TEMPLATE, previous)

    /**
     * Get a [menu] with an optional [previous] menu that opens when the main [menu] is closed
     */
    private fun <T : Menu> getMenu(menu: T, previous: Menu?): T {
        return if (previous == null) menu else menu.apply {
            setCloseHandler { p, _ ->
                previous.open(p)
            }
        }
    }

    /**
     * When [slot] is clicked open the [leftClick] menu.
     *
     * If the player right clicks and [rightClick] is provided [rightClick] will be opened instead of [leftClick]
     */
    private fun openScreenOnClick(slot: Slot, leftClick: Menu, rightClick: Menu? = null) {
        slot.setClickHandler { player, clickInfo ->
            val type = clickInfo.clickType

            if (rightClick != null && type.isRightClick) rightClick.open(player)
            else leftClick.open(player)
        }
    }
}