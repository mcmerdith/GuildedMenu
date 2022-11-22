package net.mcmerdith.guildedmenu.gui.towny

import com.palmergames.bukkit.towny.TownyFormatter
import com.palmergames.bukkit.towny.`object`.Resident
import com.palmergames.bukkit.towny.`object`.Town
import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.util.ItemStackUtils.addLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.MenuSelectReceiver
import net.mcmerdith.guildedmenu.util.PlayerUtils.getHeadItem
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings

/**
 * View all residents of [town]
 */
class TownResidentViewer(
    private val previous: MenuProvider?,
    private val town: Town,
    private val selectReceiver: MenuSelectReceiver<Resident>? = null
) : PaginatedMenu() {
    override fun getBuilder() = BaseMenu.Builder(4).title("${town.name} residents").previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(3)

    override fun setup(builder: PaginatedMenuBuilder) {
        for (resident in town.residents) {
            // Add all residents
            builder.addItem(SlotSettings.builder().apply {
                item(
                    (resident.player?.getHeadItem() ?: SkullCreator.createSkull().setName(resident.name))
                        .addLore(
                            "Ranks: ${resident.townRanks.joinToString(", ")}",
                            "Joined: ${TownyFormatter.registeredFormat.format(resident.joinedTownAt)}",
                            "Last Online: ${TownyFormatter.lastOnlineFormat.format(resident.lastOnline)}",
                            "Registered: ${TownyFormatter.registeredFormat.format(resident.registered)}"
                        )
                )

                clickHandler { player, _ ->
                    if (selectReceiver?.invoke(player, resident) == true) player.closeInventory()
                }
            }.build())
        }
    }
}