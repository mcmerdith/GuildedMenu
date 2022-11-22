package net.mcmerdith.guildedmenu.integration

import org.bukkit.Bukkit

abstract class Integration constructor(
    val pluginName: String
) {
    /**
     * Check if the integration can be used
     *
     * @return If the integration can be used
     */
    val pluginEnabled: Boolean
        get() = Bukkit.getPluginManager().isPluginEnabled(pluginName)

    /**
     * Check if the required plugin is enabled
     */
    val pluginInstalled: Boolean
        get() = Bukkit.getPluginManager().getPlugin(pluginName) != null

    var ready = false
        private set
        get() = field && pluginEnabled

    fun setError() {
        ready = false
    }

    fun resetError() {
        ready = true
    }

    /**
     * Load the integration
     */
    open fun setup() {

    }

    /**
     * Run when server is loading. A
     *
     * @return If the integration enabled successfully
     */
    abstract fun onEnable(): Boolean
}