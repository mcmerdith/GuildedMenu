package net.mcmerdith.guildedmenu.integration

import com.palmergames.bukkit.towny.TownyUniverse
import com.palmergames.bukkit.towny.`object`.Town
import com.palmergames.bukkit.towny.`object`.WorldCoord
import net.mcmerdith.guildedmenu.util.Extensions.asPlayer
import net.mcmerdith.guildedmenu.util.Extensions.asTownyResident
import java.util.*

class TownyIntegration : Integration("Towny") {
    override fun onEnable(): Boolean {
        return false
    }

    fun getAPI(): TownyUniverse = TownyUniverse.getInstance()

    /**
     * /t [name]
     * Get a town named [name]
     */
    fun getTown(name: String): Town? = getAPI().getTown(name)

    /**
     * /t list
     * List all towns
     */
    fun allTowns(): Collection<Town> = getAPI().towns

    /**
     * /t
     * Get a players home town
     */
    fun homeTown(player: UUID): Town? = player.asTownyResident()?.townOrNull

    /**
     * /t here
     * Get the town the player is currently in
     */
    fun currentTown(player: UUID): Town? {
        val location = player.asPlayer()?.location ?: return null
        return getAPI().getTownBlock(WorldCoord.parseWorldCoord(location)).town
    }

    /**
     * /t leave
     * [player] leaves their town
     */
    fun leaveTown(player: UUID) = player.asTownyResident()?.removeTown()

    /**
     * /t claim
     * Claim town blocks for [player]
     */
    fun townClaim(player: UUID, outpost: Boolean = false, radius: Int = -1, auto: Boolean = false) {
        // Validate input paramaters
        if (outpost && auto) throw IllegalArgumentException("Cannot auto-claim an outpost")
        if (radius > -1 && (auto || outpost)) throw IllegalArgumentException("Radius cannot be used with the outpost or auto-claim modes")
        // Construct a claim command
        var command = "/towny:town claim "
        player.asPlayer()?.performCommand(command) ?: run {

        }
    }

    /**
     * /t outpost
     */
    fun townOutpost() {

    }


    /**
     * /t delete
     */
    fun deleteTown() {}

    /**
     * /t spawn [town]
     * Teleport [player] to the spawn of [town]
     */
    fun townSpawn(town: Town, player: UUID) {

    }


    /**
     * /t reslist
     */
    fun townResidentList() {

    }

    /**
     * /t ranklist [name]
     */
    fun townRankList(name: String) {}

    /**
     * /t outlawlist [name]
     */
    fun outlawList(name: String) {

    }

    /**
     * /t rank [action] [resident] [rank]
     * [action] add/remove (true/false)
     * [rank] from [resident]
     */
    fun townRankUpdate(action: Boolean, resident: UUID, rank: String) {

    }

    /**
     * /t deposit [amount]
     */
    fun townDeposit(amount: Double) {}

    /**
     * /t toggle
     */
    fun townToggle() {

    }

    /**
     * /t set
     */
    fun townSet() {}

}