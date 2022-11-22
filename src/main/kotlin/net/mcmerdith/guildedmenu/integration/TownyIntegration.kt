package net.mcmerdith.guildedmenu.integration

import com.palmergames.bukkit.towny.Towny
import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.command.TownCommand
import com.palmergames.bukkit.towny.`object`.Resident
import com.palmergames.bukkit.towny.`object`.Town
import com.palmergames.bukkit.towny.`object`.TownyPermission
import com.palmergames.bukkit.towny.`object`.TownyPermission.ActionType
import com.palmergames.bukkit.towny.`object`.TownyPermission.PermLevel
import com.palmergames.bukkit.towny.permissions.TownyPerms
import com.palmergames.bukkit.towny.utils.MoneyUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.toSentenceCase
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

    fun getResident(uuid: UUID) = api.getResident(uuid)

    /**
     * /t claim [shape] [count]
     *
     * Claim town blocks for [player]
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
     * /t claim auto
     *
     * Claim maximum town blocks for [player]
     */
    fun townClaimAuto(player: Player) =
        TownCommand.parseTownClaimCommand(
            player,
            arrayOf("auto")
        )

    /**
     * /t claim outpost
     *
     * Claim output for [player]
     */
    fun townClaimOutpost(player: Player) =
        TownCommand.parseTownClaimCommand(
            player,
            arrayOf("outpost")
        )

    /**
     * /t outpost
     *
     * Teleport [player] to spawn of [outpostId]
     */
    fun townOutpostSpawn(player: Player, outpostId: Int) =
        TownCommand.townSpawn(player, arrayOf(outpostId.toString()), true, false)

    /**
     * /t delete [town]
     *
     * Delete a town
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
     * /t spawn [town]
     *
     * Teleport [player] to the spawn of [town]
     *
     * [player]'s hometown will be used if [town] is null
     */
    fun townSpawn(player: Player, town: Town?) {
        TownCommand.townSpawn(
            player,
            town?.let { arrayOf(it.name) } ?: arrayOf(),
            false,
            false
        )
    }

    /**
     * Get all town ranks
     */
    fun getTownRanks(): List<String> = TownyPerms.getTownRanks()

    /**
     * Get all nation ranks
     */
    fun getNationRanks(): List<String> = TownyPerms.getNationRanks()

    /**
     * /t ranklist [town]
     *
     * Returns a map of each rank to a list of [Resident]s with that rank
     */
    fun townRankList(town: Town): Map<String, List<Resident>> {
        val res = mutableMapOf<String, List<Resident>>()

        for (rank in getTownRanks()) {
            res[rank] = town.getRank(rank)
        }

        return res
    }

    /**
     * /t rank [action] [target] [rank]
     * [action]: true=add, false=remove
     * Add/remove [rank] from [target]
     */
    fun townRankUpdate(player: Player, action: Boolean, target: Player, rank: String) {
        townCommand.townRank(
            player, arrayOf(
                if (action) "add" else "remove",
                target.name,
                rank
            )
        )
    }

    /**
     * /t deposit [amount]
     *
     * Deposit [amount] to [player]'s town
     */
    fun townDeposit(player: Player, resident: Resident, town: Town, amount: Int) {
        MoneyUtil.townDeposit(player, resident, town, null, amount)
    }

    /**
     * /t deposit [amount]
     *
     * Deposit [amount] to [player]'s town
     */
    fun townWithdraw(player: Player, resident: Resident, town: Town, amount: Int) {
        MoneyUtil.townWithdraw(player, resident, town, amount)
    }

    /**
     * t set board
     */
    fun townSetBoard(player: Player, town: Town, board: String) {
        TownCommand.townSet(player, arrayOf("board", *board.split(" ").toTypedArray()), false, town)
    }

    fun townSetPerm(player: Player, action: ActionType, level: PermLevel, value: Boolean, town: Town) {
        TownCommand.townSet(
            player, arrayOf(
                "perm", level.name, action.name,
                if (value) "on" else "off"
            ), false, town
        )
    }

    fun getPermissionsItem(action: ActionType, town: Town): ItemStack {
        val lore = mutableListOf<String>()

        town.permissions.let { tPerm ->
            for (perm in TownyPermission.PermLevel.values()) {
                lore.add(
                    "${perm.name.toSentenceCase()}: ${
                        if (tPerm.getPerm(perm, action)) "${ChatColor.GREEN}Yes"
                        else "${ChatColor.RED}No"
                    }"
                )
            }
        }

        return ItemTemplates.UI.getInfo(action.commonName).setLore(lore)
    }

    fun getPermissionItem(action: ActionType, level: PermLevel, town: Town): ItemStack {
        val allowed = town.permissions.getPerm(level, action)

        return ItemStack(if (allowed) Material.GREEN_CONCRETE else Material.RED_CONCRETE)
            .setName(action.commonName)
            .setLore(
                "${level.name.toSentenceCase()}: ${
                    if (allowed) "${ChatColor.GREEN}Yes"
                    else "${ChatColor.RED}No"
                }"
            )
    }

    enum class Settings(val description: String) {
        EXPLOSION("Explosions"),
        FIRE("Fire Spread"),
        MOBS("Mob Spawning"),
        PUBLIC("Public Spawn TP and Visible Coordinates"),
        PVP("PVP"),
        TAXPERCENT("Taxation (percent)"),
        NATIONZONE("Nation Zone?"),
        OPEN("Public Joining");

        fun getItem(enabled: Boolean): ItemStack {
            return if (enabled) {
                ItemStack(Material.LIME_CONCRETE).setName(description).setLore("${ChatColor.GREEN}Enabled")
            } else {
                ItemStack(Material.RED_CONCRETE).setName(description).setLore("${ChatColor.RED}Disabled")
            }
        }

        fun toggle(player: Player, town: Town) {
            TownCommand.townToggle(player, arrayOf(name), false, town)
        }
    }
}