package net.mcmerdith.guildedmenu.util

import com.google.gson.Gson
import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.integration.Integration
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.util.*
import java.util.stream.Collectors

object Globals {
    private lateinit var configDir: File

    val PERMISSION_ADMIN = "guildedmenu.admin"

    val gson = Gson()

    fun init(main: GuildedMenu) {
        configDir = main.dataFolder
        if (!configDir.exists()) configDir.mkdir()
    }

    // Integrations
    /**
     * Get the directory integration configs are stored in
     *
     * @return A [File] or null if one doesn't exist
     */
    val integrationConfigDir: File?
        get() {
            val dir = File(configDir, "integrations" + File.separator)
            var success = true
            if (!dir.exists()) success = dir.mkdir()
            return if (success) dir else null
        }

    /**
     * Get an integrations config file
     *
     * @param integration The integration
     * @param name        The name of the config file
     * @return A [File] or null if one doesn't exist
     */
    fun getIntegrationConfigFile(integration: Integration, name: String): File? {
        val dir = integrationConfigDir ?: return null
        val datafile = File(dir, integration.pluginName + File.separator + name + ".yml" + File.separator)
        return try {
            if (!datafile.exists()) GuildedMenu
                .plugin.saveResource("integration/" + integration.pluginName + "/" + name + ".yml", false)
            datafile
        } catch (e: IllegalArgumentException) {
            GuildedMenu.plugin.logger
                .warning("No config found for name '" + name + "' in integration " + integration.pluginName)
            null
        }
    }
    // Panel Functions
    /**
     * Get the directory panels are stored in
     *
     * @return A [File] or null if one doesn't exist
     */
    val panelConfigDir: File?
        get() {
            val dir = File(configDir, "panels" + File.separator)
            var success = true
            if (!dir.exists()) success = dir.mkdir()
            return if (success) dir else null
        }

    /**
     * Get a panels config file
     *
     * @param name The name of the panel
     * @return A [File] or null if one doesn't exist
     */
    fun getPanelFile(name: String): File? {
        val dir = panelConfigDir ?: return null
        val datafile = File(dir, name + ".yml" + File.separator)
        return try {
            if (!datafile.exists()) GuildedMenu.plugin.saveResource("panels/$name.yml", true)
            datafile
        } catch (e: IllegalArgumentException) {
            GuildedMenu.plugin.logger.warning("No panel found for name '$name'")
            null
        }
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