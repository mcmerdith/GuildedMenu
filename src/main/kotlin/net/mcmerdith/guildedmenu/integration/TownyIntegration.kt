package net.mcmerdith.guildedmenu.integration

import com.palmergames.bukkit.towny.Towny
import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.command.TownCommand
import com.palmergames.bukkit.towny.confirmations.ConfirmationHandler
import com.palmergames.bukkit.towny.`object`.Nation
import com.palmergames.bukkit.towny.`object`.Resident
import com.palmergames.bukkit.towny.`object`.Town
import com.palmergames.bukkit.towny.`object`.TownyPermission
import com.palmergames.bukkit.towny.`object`.TownyPermission.ActionType
import com.palmergames.bukkit.towny.`object`.TownyPermission.PermLevel
import com.palmergames.bukkit.towny.permissions.TownyPerms
import com.palmergames.bukkit.towny.utils.MoneyUtil
import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.PlayerUtils.canInherentlyEdit
import net.mcmerdith.guildedmenu.util.PlayerUtils.isTownyAdmin
import net.mcmerdith.guildedmenu.util.capitalize
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class TownyIntegration : Integration("Towny") {
    override fun onEnable(): Boolean {
        return true
    }

    private val api: TownyAPI by lazy { TownyAPI.getInstance() }
    private val townCommand: TownCommand by lazy { TownCommand(Towny.getPlugin()) }

    /**
     * Get a [Resident] for [uuid]
     *
     * Returns null if Towny does not have a [Resident] registered for [uuid]
     */
    fun getResident(uuid: UUID) = api.getResident(uuid)

    /**
     * Get all town ranks
     */
    fun getTownRanks(): List<String> = TownyPerms.getTownRanks()

    /**
     * Get all nation ranks
     */
    fun getNationRanks(): List<String> = TownyPerms.getNationRanks()

    fun getTowns(): List<Town> = api.towns

    fun getNations(): List<Nation> = getTowns().mapNotNull { town -> town.nationOrNull }.distinct()

    fun townJoin(player: Player, town: Town) {
        TownCommand.parseTownJoin(player, arrayOf(town.name))
    }

    fun townInitiateLeave(player: Player, menu: MenuProvider? = null) {
        townCommand.townLeave(player)

        // Build the confirmation menu
        BaseMenu.Builder(3).title("Are you sure?").previous(menu).build().apply {
            getSlot(2, 4).apply {
                item = ItemTemplates.UI.getCheckMark("Yes, leave town")
                setClickHandler { p, _ ->
                    if (ConfirmationHandler.hasConfirmation(p)) {
                        ConfirmationHandler.acceptConfirmation(p)
                    } else {
                        p.sendErrorMessage("Could not leave town")
                    }
                    p.closeInventory()
                }
            }

            getSlot(2, 6).apply {
                item = ItemTemplates.UI.getXMark("No, cancel")
                setClickHandler { p, _ ->
                    if (ConfirmationHandler.hasConfirmation(p)) {
                        ConfirmationHandler.revokeConfirmation(p)
                    }

                    p.closeInventory()
                }
            }
        }.open(player)
    }

    /**
     * Claim [count] town blocks for [player]
     *
     * [shape]: true=rect, false=circle
     */
    fun townClaim(player: Player, count: Int, shape: Boolean = true) =
        TownCommand.parseTownClaimCommand(
            player,
            arrayOf(
                if (shape) "rect" else "circle",
                if (count < 0) "auto" else count.toString()
            )
        )

    /**
     * Claim maximum town blocks for [player]
     */
    fun townClaimAuto(player: Player) =
        TownCommand.parseTownClaimCommand(
            player,
            arrayOf("auto")
        )

    /**
     * Claim output for [player]
     */
    fun townClaimOutpost(player: Player) =
        TownCommand.parseTownClaimCommand(
            player,
            arrayOf("outpost")
        )

    /**
     * Teleport [player] to spawn of [outpostId]
     */
    fun townOutpostSpawn(player: Player, outpostId: Int) =
        TownCommand.townSpawn(player, arrayOf(outpostId.toString()), true, false)

    /**
     * Delete a [town]
     *
     * [player]'s town will be used if [town] is not provided
     */
    fun deleteTown(player: Player, town: Town? = null) {
        townCommand.townDelete(
            player,
            town?.let { arrayOf(town.name) } ?: arrayOf()
        )
    }

    /**
     * Teleport [player] to the spawn of [town]
     *
     * [player]'s hometown will be used if [town] is null
     */
    fun townSpawn(player: Player, town: Town? = null) {
        TownCommand.townSpawn(
            player,
            town?.let { arrayOf(it.name) } ?: arrayOf(),
            false,
            false
        )
    }

    /**
     * Returns a map of each rank for [town] to a list of [Resident]s with that rank
     */
    fun townRankList(town: Town): Map<String, List<Resident>> {
        val res = mutableMapOf<String, List<Resident>>()

        for (rank in getTownRanks()) {
            res[rank] = town.getRank(rank)
        }

        return res
    }

    /**
     * [player]: The calling player
     *
     * [action]: true=add, false=remove
     *
     * Add/remove [rank] from [target] ([player] if not provided)
     */
    fun townRankUpdate(player: Player, action: Boolean, target: Player?, rank: String) {
        townCommand.townRank(
            player, arrayOf(
                if (action) "add" else "remove",
                target?.name ?: player.name,
                rank
            )
        )
    }

    /**
     * [player]: The calling player
     *
     * Deposit [amount] to [town] from [resident]'s account
     */
    fun townDeposit(player: Player, resident: Resident, town: Town, amount: Int) {
        MoneyUtil.townDeposit(player, resident, town, null, amount)
    }

    /**
     * [player]: The calling player
     *
     * Withdraw [amount] from [town] to [resident]'s account
     */
    fun townWithdraw(player: Player, resident: Resident, town: Town, amount: Int) {
        MoneyUtil.townWithdraw(player, resident, town, amount)
    }

    /**
     * [player]: The calling player
     *
     * Set [town]'s board to [board]
     */
    fun townSetBoard(player: Player, town: Town, board: String) {
        townSet(town, player, "board", *board.split(" ").toTypedArray())
    }

    /**
     * [player]: The calling player
     *
     * Set [town]'s [action] permission for [level] to [value]
     */
    fun townSetPerm(player: Player, action: ActionType, level: PermLevel, value: Boolean, town: Town) =
        townSet(town, player, "perm", level.name, action.name, if (value) "on" else "off")

    /**
     * [player]: The calling player
     *
     * Set [town]'s mayor to [mayor]
     */
    fun townSetMayor(player: Player, mayor: Resident, town: Town) =
        townSet(town, player, "mayor", mayor.name)


    /**
     * Set [key] to [value] on [town]
     *
     * [player] is the calling player.
     * If [player] is not inherently able to edit [town] but is an admin the set will be run as the mayor
     */
    fun townSet(town: Town, player: Player, key: String, vararg value: String) {
        TownCommand.townSet(
            player,
            arrayOf(key, *value),
            !player.canInherentlyEdit(town) && player.isTownyAdmin(),
            town
        )
    }

    /**
     * Get an item representing [town]'s current permission level for [action]
     */
    fun getPermissionsItem(action: ActionType, town: Town): ItemStack {
        val lore = mutableListOf<String>()

        town.permissions.let { tPerm ->
            for (perm in TownyPermission.PermLevel.values()) {
                lore.add(
                    "${perm.name.capitalize()}: ${
                        if (tPerm.getPerm(perm, action)) "${ChatColor.GREEN}Yes"
                        else "${ChatColor.RED}No"
                    }"
                )
            }
        }

        return ItemTemplates.UI.getInfo(action.commonName).setLore(lore)
    }

    /**
     * Get an item representing [town]'s current permission of [level] for [action]
     */
    fun getPermissionItem(action: ActionType, level: PermLevel, town: Town): ItemStack {
        val allowed = town.permissions.getPerm(level, action)

        return ItemStack(if (allowed) Material.GREEN_CONCRETE else Material.RED_CONCRETE)
            .setName(action.commonName)
            .setLore(
                "${level.name.capitalize()}: ${
                    if (allowed) "${ChatColor.GREEN}Yes"
                    else "${ChatColor.RED}No"
                }"
            )
    }

    enum class Settings(val description: String) {
        EXPLOSION("Explosions"),
        FIRE("Fire Spread"),
        MOBS("Mob Spawning"),
        PUBLIC("Public Spawn"),
        PVP("PVP"),
        TAXPERCENT("Taxation (percent)"),
        NATIONZONE("Nation Zone?"),
        OPEN("Public Joining");

        /**
         * Get an item representing this setting if [enabled]
         */
        fun getItem(enabled: Boolean): ItemStack {
            return if (enabled) {
                ItemStack(Material.LIME_CONCRETE).setName(description).setLore("${ChatColor.GREEN}Enabled")
            } else {
                ItemStack(Material.RED_CONCRETE).setName(description).setLore("${ChatColor.RED}Disabled")
            }
        }

        /**
         * [player]: The calling player
         *
         * Toggle this setting for [town]
         */
        fun toggle(player: Player, town: Town) {
            TownCommand.townToggle(player, arrayOf(name), false, town)
        }
    }

    companion object {
        fun Town.getIcon(): ItemStack {
            // TODO read icon from metadata
            return SkullCreator.itemFromUuid(mayor.uuid).setName(name).setLore("Mayor: ${mayor.name}")
        }
    }
}