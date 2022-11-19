package net.mcmerdith.guildedmenu.util

import com.google.gson.Gson
import net.mcmerdith.guildedmenu.GuildedMenu
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.util.*
import java.util.stream.Collectors

object Globals {
    private lateinit var configDir: File

    @Suppress("unused")
    val DEBUG_PLAYER: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(UUID.fromString("a8ae1005-73e3-49ba-b94e-bbf5143451bb"))

    object PERMISSION {
        val ADMIN = "guildedmenu.admin"
    }

    val gson = Gson()

    fun init(main: GuildedMenu) {
        configDir = main.dataFolder
        if (!configDir.exists()) configDir.mkdir()
    }

    // Business Data
    /**
     * Get the directory business are stored in
     *
     * @return A [File] or null if one doesn't exist
     */
    val businessConfigDir: File?
        get() {
            val dir = File(configDir, "business" + File.separator)
            var success = true
            if (!dir.exists()) success = dir.mkdir()
            return if (success) dir else null
        }

    /**
     * Get a business' config file
     *
     * @param id         The name of the business
     * @param noValidate Don't create the file if it doesn't exist
     * @return A [File] or null if one doesn't exist
     */
    fun getBusinessConfigFile(id: String?, noValidate: Boolean = false): File? {
        val dir = businessConfigDir ?: return null
        val datafile = File(dir, id + ".json" + File.separator)
        if (noValidate) return datafile
        var success = true
        try {
            if (!datafile.exists()) success = datafile.createNewFile()
        } catch (e: IOException) {
            success = false
            GuildedMenu.plugin.logger.warning("Could not create new datafile for business '$id'")
        }
        return if (success) datafile else null
    }// No business directory

    // Can't list files
    /**
     * Get a list of all business IDs
     *
     * @return A list of the IDs
     */
    val allBusinessIds: List<String>
        get() {
            val dir = businessConfigDir ?: return ArrayList()

            // No business directory
            val filter = FilenameFilter { dir1: File?, name: String -> name.endsWith(".json") }
            val files = dir.listFiles(filter) ?: return ArrayList()

            // Can't list files
            return Arrays.stream(files).map { file: File -> file.name.substring(0, file.name.length - 5) }
                .collect(Collectors.toList())
        }
}