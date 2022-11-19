package net.mcmerdith.guildedmenu.business

import com.google.gson.JsonSyntaxException
import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.configuration.FileHandler
import net.mcmerdith.guildedmenu.util.Globals
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.util.*

class Business {
    var name: String? = null
    var id: String? = null
    var owner: UUID? = null
    var managers: List<UUID>? = null

    private constructor(name: String, owner: UUID, managers: List<UUID>?) {
        this.name = name
        id = name.replace("\\W+".toRegex(), "")
        this.owner = owner
        this.managers = managers ?: ArrayList()
    }

    /**
     * Gson Constructor
     */
    @Suppress("unused")
    private constructor()

    /**
     * Check if a Player is a manager OR has the "gm.business.admin" permission
     *
     * @param player The Player
     * @return If the Player is a manager
     */
    fun isManager(player: Player): Boolean {
        return player.hasPermission("gm.business.admin") || isManager(player)
    }

    /**
     * Check if an OfflinePlayer is a manager. Permissions are NOT checked
     *
     * @param player The OfflinePlayer
     * @return If the OfflinePlayer is a manager
     */
    fun isManager(player: OfflinePlayer): Boolean {
        val pUUID = player.uniqueId
        return pUUID == owner || managers!!.contains(pUUID)
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
        val datafile = Globals.getBusinessConfigFile(id) ?: return false
        // Fail if the file couldn't be created
        return try {
            // Serialize and write the data
            FileHandler.writeDataFile(Globals.gson.toJson(this), datafile)
            true
        } catch (e: IOException) {
            GuildedMenu.plugin.logger
                .warning("Error saving business to '" + datafile.name + "': The file could not be read, or doesn't exist")
            e.printStackTrace()
            false
        } catch (e: JsonSyntaxException) {
            GuildedMenu.plugin.logger.warning("Error saving business to '" + datafile.name + "': Invalid JSON")
            e.printStackTrace()
            false
        }
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
            // The original filename
            val originalID = business.id
            var i = 0
            do {
                if (i > 0) {
                    // Second or later pass
                    business.id = originalID + i // Add an index and try again
                }
                // Increment our index
                i++

                // Get the config file
                rawFile = Globals.getBusinessConfigFile(business.id, true)
            } while (rawFile != null && rawFile.exists()) // Continue only if file is available

            // Save the business to disk
            return if (!business.save()) {
                // Return null if the save fails
                null
            } else business

            // Return the new business
        }

        /**
         * Load a business from disk
         *
         * @param name The name of the business
         * @return The business or null if 1. The business doesn't exist OR 2. The business is invalid
         */
        fun load(name: String): Business? {
            // Get the config file
            val datafile = Globals.getBusinessConfigFile(name) ?: return null

            return try {
                // Read and deserialize the data
                val b = Globals.gson.fromJson(FileHandler.readDataFile(datafile), Business::class.java)

                // Fail if any required elements are missing
                if (b.name == null || b.id == null || b.owner == null) null else b
            } catch (e: IOException) {
                GuildedMenu.plugin.logger
                    .warning("Error loading business from '${datafile.name}': The file could not be read, or doesn't exist")
                e.printStackTrace()
                null
            } catch (e: JsonSyntaxException) {
                GuildedMenu.plugin.logger.warning("Error loading business from '${datafile.name}': Invalid JSON")
                e.printStackTrace()
                null
            }
        }
    }
}