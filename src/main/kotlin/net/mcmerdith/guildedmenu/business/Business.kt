package net.mcmerdith.guildedmenu.business

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import net.mcmerdith.guildedmenu.util.GMLogger
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.PlayerUtils.getPlayerHead
import net.mcmerdith.guildedmenu.util.PlayerUtils.isAdmin
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.IOException
import java.util.*

class Business private constructor() {
    @Transient
    var id: UUID? = null
        private set

    @Transient
    private var file: File? = null
    var name: String? = null
    var owner: UUID? = null
    var managers = mutableListOf<UUID>()
    var locations = mutableListOf<BusinessLocation>()
    var icon: Material? = null

    /**
     * ONLY USE TO ALLOCATE A NEW BUSINESS
     *
     * Load existing businesses with [Business.load]
     */
    private constructor(name: String, owner: UUID, managers: List<UUID>?) : this() {
        this.name = name
        this.owner = owner
        if (managers != null) this.managers.addAll(managers)
    }

    /**
     * Assign this business a new [UUID]
     */
    private fun newID(): UUID {
        id = UUID.randomUUID()
        return id!!
    }

    /**
     * If [player] is the owner of this business
     */
    fun isOwner(player: Player): Boolean {
        return player.isAdmin() || isOwner(player as OfflinePlayer)
    }

    /**
     * If [player] is the owner of this business
     */
    fun isOwner(player: OfflinePlayer) = player.uniqueId == owner

    /**
     * Check if a Player is a manager OR has the "gm.business.admin" permission
     *
     * @param player The Player
     * @return If the Player is a manager
     */
    fun isManager(player: Player): Boolean {
        return player.isAdmin() || isManager(player as OfflinePlayer)
    }

    /**
     * Check if an OfflinePlayer is a manager. Permissions are NOT checked
     *
     * @param player The OfflinePlayer
     * @return If the OfflinePlayer is a manager
     */
    fun isManager(player: OfflinePlayer) = isOwner(player) || managers.contains(player.uniqueId)

    /**
     * Get this business' icon
     */
    fun getIcon(): ItemStack {
        val base = icon?.let { ItemStack(it) } ?: owner!!.getPlayerHead()

        return base.setName(name)
    }

    /**
     * Add a new [location]
     *
     * Returns false if [location] is already registered
     */
    fun addLocation(location: BusinessLocation): Boolean {
        if (locations.find { location.isSimilar(it) } != null) return false

        locations.add(location)
        save()

        return true
    }

    /**
     * Save the business to disk
     *
     * Returns false if there was an error saving
     */
    fun save(): Boolean {
        // Fail if any required elements are missing
        if (name == null || id == null || owner == null) return false

        // Get the file
        return try {
            // Serialize and write the data
            FileHandler.writeDataFile(gson.toJson(this), file!!)
            true
        } catch (e: IOException) {
            GMLogger.FILE.error(
                "Error saving business to '${file!!.name}'",
                e
            )
            false
        }
    }

    /**
     * Delete this business
     *
     * Unregisters with [BusinessManager] and deletes file on disk
     */
    fun delete() {
        BusinessManager.deregister(this)
        file!!.delete()
    }

    companion object {
        private val gson = Gson()

        /**
         * Create a new business named [name] owned by [owner] with [managers]
         *
         * Saves to disk automatically
         *
         * Returns the business or null if there was an error
         */
        fun create(name: String, owner: UUID, managers: List<UUID>?): Business? {
            // Create the business
            val business = Business(name, owner, managers)

            // Get an available config file
            var rawFile: File?

            do {
                // Get the config file
                rawFile = FileHandler.getBusinessConfigFile(business.newID())
            } while (rawFile != null && rawFile.exists()) // Continue only if file is available

            business.file = rawFile

            // Save the business to disk
            return if (business.save()) business
            else null
        }

        /**
         * Load business [id] from disk
         *
         * Return the business or null if it doesn't exist or is invalid
         */
        fun load(id: UUID): Business? {
            // Get the config file
            val datafile = FileHandler.getBusinessConfigFile(id)

            return try {
                // Read and deserialize the data
                val b = gson.fromJson(FileHandler.readDataFile(datafile), Business::class.java)

                // Fail if any required elements are missing
                if (b.name == null || b.owner == null) throw JsonSyntaxException("Missing required field `name` or `owner`")

                // Set the id, file and return
                b.id = id
                b.file = datafile

                b
            } catch (e: IOException) {
                GMLogger.FILE.error(
                    "Error loading business from '${datafile.name}'",
                    e
                )

                null
            } catch (e: JsonSyntaxException) {
                GMLogger.FILE.error(
                    "Error loading business from '${datafile.name}': Invalid JSON",
                    e
                )

                null
            }
        }
    }
}