package net.mcmerdith.guildedmenu.integration

import net.mcmerdith.guildedmenu.util.Globals
import java.util.HashMap
import kotlin.jvm.JvmOverloads
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration

abstract class Integration constructor(
    val pluginName: String
) {
    /**
     * Check if the integration can be used
     *
     * @return If the integration can be used
     */
    val isAvailable: Boolean
        get() = Bukkit.getPluginManager().isPluginEnabled(pluginName)

    /**
     * Load the integration
     */
    fun setup() {}

    /**
     * Run when server is loading. A
     *
     * @return If the integration enabled successfully
     */
    abstract fun onEnable(): Boolean
}