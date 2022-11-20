package net.mcmerdith.guildedmenu.business

import com.google.gson.JsonSyntaxException
import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.business.BusinessManager.gson
import net.mcmerdith.guildedmenu.util.Extensions.isAdmin
import net.mcmerdith.guildedmenu.util.Extensions.setName
import net.mcmerdith.guildedmenu.util.GMLogger
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

    fun getIcon(): ItemStack {
        val base = icon?.let { ItemStack(it) } ?: SkullCreator.itemFromUuid(owner!!)

        return base.setName(name)
    }

    /**
     * Save the business to disk
     *
     * @return False for file system error, True otherwise
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

    fun delete() {
        BusinessManager.deregister(this)
        file!!.delete()
    }

    companion object {
        /**
         * Create a new business and save it to disk
         *
         * @param name     The name of the business
         * @param owner    The owners UUID
         * @param managers A list of the UUIDs allowed to manage this business
         * @return The new business, OR null if the business could not be created
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
         * Load a business from disk
         *
         * @param id The name of the business
         * @return The business or null if 1. The business doesn't exist OR 2. The business is invalid
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