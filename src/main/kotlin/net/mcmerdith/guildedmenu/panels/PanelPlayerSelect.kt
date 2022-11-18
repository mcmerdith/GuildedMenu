package net.mcmerdith.guildedmenu.panels

import net.mcmerdith.guildedmenu.panels.builders.CPItemBuilder
import net.mcmerdith.guildedmenu.panels.builders.CPPanelBuilder
import net.mcmerdith.guildedmenu.panels.gui.PageData
import net.mcmerdith.guildedmenu.util.Globals
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

/**
 * Player selection GUI
 */
object PanelPlayerSelect {
    /**
     * Get the first page of online players
     *
     * @param callback A CommandPanels 'event=' tag that will be called by: 'event={callback} playerUUID'
     * @return A CommandPanels configuration with a list of players
     */
//    fun get(event: IPlayerSelectReceiver): ComboPanel {
//        return PanelPlayerSelect[0, true, event]
//    }

    /**
     * Get a page of players
     * <br></br>A single page list can contain up to 54 elements. More than 54 elements will be split into groups of 45 to allow for forward/back controls
     *
     * @param page     The page number requested
     * @param online   Only show online players
     * @param callback An [IPlayerSelectReceiver]
     * @return A [ComboPanel] with the GUI on top, and the Controls on the bottom (if applicable)
     */
    operator fun <T> get(page: Int = 0, online: Boolean = true, callback: T): ComboPanel where T : PanelEvent, T : IPlayerSelectReceiver {
        // Get the base panel config and load it
        val builder = CPPanelBuilder(null, "playerselect")
            .setTitle("Player Select Menu")
            .setPanelStatic().setPanelItemsImmovable()

        var players = Bukkit.getOfflinePlayers().sortedBy { it.name }.toMutableList()
        if (online) players = players.filter { it.isOnline }.toMutableList()

        val debug: OfflinePlayer = Bukkit.getOfflinePlayer("mcmerdith")
        for (i in 1..(106 - players.size)) {
            players.add(debug)
        }

        val pageData = PageData(players, page)
        builder.setRows(pageData.rows)

        // Set all the items
        for (i in 0 until pageData.totalCount) {
            val player = players[i]

            // Set the YAML key to our current index
            val headItem = CPItemBuilder()
                .setMaterial("cps= " + player!!.name)
                .setName(player.name)
                .addCommand("cpc")

            callback.playerSelected(player.uniqueId)

            Globals.applyEventCommand(headItem, callback)

            builder.setItem(i + pageData.offset(i), headItem)
        }

        var controlPanel: CPPanelBuilder? = null

        if (pageData.hasPages) controlPanel = CPPanelBuilder.getControlsPanel(
            pageData,
            PlayerSelectEvent(online, page)
        )

        return ComboPanel(builder, null, controlPanel)
    }
}