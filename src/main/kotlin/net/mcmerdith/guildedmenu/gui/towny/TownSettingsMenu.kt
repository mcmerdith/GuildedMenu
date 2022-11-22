package net.mcmerdith.guildedmenu.gui.towny

import com.palmergames.bukkit.towny.`object`.Town
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.BasicMenu
import net.mcmerdith.guildedmenu.integration.TownyIntegration
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.PlayerUtils.asTownyResident
import org.ipvp.canvas.slot.SlotSettings

/**
 * View settings for [town]
 */
class TownSettingsMenu(
    private val previous: MenuProvider?,
    private val town: Town
) : BasicMenu() {
    override fun getBuilder() = BaseMenu.Builder(1)
        .title("Settings (${town.name})")
        .redraw(true)
        .previous(previous)

    override fun setup(menu: BaseMenu) {
        // Add all settings items
        menu.getSlot(1, 1).settings = getSettingItem(TownyIntegration.Settings.EXPLOSION, town.isBANG)
        menu.getSlot(1, 2).settings = getSettingItem(TownyIntegration.Settings.FIRE, town.isFire)
        menu.getSlot(1, 3).settings = getSettingItem(TownyIntegration.Settings.MOBS, town.hasMobs())
        menu.getSlot(1, 4).settings = getSettingItem(TownyIntegration.Settings.PVP, town.isPVP)
        menu.getSlot(1, 5).settings = getSettingItem(TownyIntegration.Settings.TAXPERCENT, town.isTaxPercentage)
        menu.getSlot(1, 6).settings = getSettingItem(TownyIntegration.Settings.NATIONZONE, town.isNationZoneEnabled)
        menu.getSlot(1, 7).settings = getSettingItem(TownyIntegration.Settings.PUBLIC, town.isPublic)
        menu.getSlot(1, 8).settings = getSettingItem(TownyIntegration.Settings.OPEN, town.isOpen)
    }

    /**
     * Get an item representing the [value] of [setting]
     */
    private fun getSettingItem(setting: TownyIntegration.Settings, value: Boolean): SlotSettings {
        return SlotSettings.builder()
            .item(setting.getItem(value))
            .clickHandler { player, _ ->
                // Only the mayor can change settings
                if (town.isMayor(player.asTownyResident())) {
                    setting.toggle(player, town)
                    get().open(player)
                }
            }
            .build()
    }
}