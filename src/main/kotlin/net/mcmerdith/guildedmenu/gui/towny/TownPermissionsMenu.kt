package net.mcmerdith.guildedmenu.gui.towny

import com.palmergames.bukkit.towny.`object`.Town
import com.palmergames.bukkit.towny.`object`.TownyPermission.ActionType
import com.palmergames.bukkit.towny.`object`.TownyPermission.PermLevel
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.PlayerUtils.asTownyResident
import org.ipvp.canvas.slot.SlotSettings

/**
 * View [town]'s permissions for [action]
 */
class TownPermissionsMenu(
    private val previous: MenuProvider?,
    private val town: Town,
    private val action: ActionType
) : BasicMenu() {
    private val towny by lazy { IntegrationManager[TownyIntegration::class.java]!! }

    override fun getBuilder() = BaseMenu.Builder(1)
        .title("${action.commonName} Permission (${town.name})")
        .redraw(true)
        .previous(previous)

    override fun setup(menu: BaseMenu) {
        // Add all permission items
        menu.getSlot(1, 3).settings = getPermissionItem(PermLevel.RESIDENT)
        menu.getSlot(1, 4).settings = getPermissionItem(PermLevel.NATION)
        menu.getSlot(1, 5).settings = getPermissionItem(PermLevel.ALLY)
        menu.getSlot(1, 6).settings = getPermissionItem(PermLevel.OUTSIDER)
    }

    /**
     * Get an item representing the current [action] permission for [level] in [town]
     */
    private fun getPermissionItem(level: PermLevel): SlotSettings {
        return SlotSettings.builder()
            .item(towny.getPermissionItem(action, level, town))
            .clickHandler { player, _ ->
                // Only the mayor can change settings
                if (town.isMayor(player.asTownyResident())) {
                    towny.townSetPerm(player, action, level, !town.permissions.getPerm(level, action), town)
                    get().open(player)
                }
            }
            .build()
    }
}