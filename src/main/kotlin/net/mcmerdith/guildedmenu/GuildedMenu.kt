package net.mcmerdith.guildedmenu

import net.mcmerdith.guildedmenu.business.BusinessManager
import net.mcmerdith.guildedmenu.configuration.MenuConfig
import net.mcmerdith.guildedmenu.configuration.PluginConfig
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.util.GMLogger
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.ipvp.canvas.MenuFunctionListener

class GuildedMenu : JavaPlugin() {
    init {
        plugin = this
    }

    companion object {
        lateinit var plugin: GuildedMenu
    }

    lateinit var config: PluginConfig
    lateinit var menuConfig: MenuConfig

    override fun onLoad() {
        super.onLoad()

        // Logging
        GMLogger.init(logger)

        // Integration preload
        IntegrationManager.setup()

        // Load data
        BusinessManager.init()
    }

    override fun onEnable() {
        super.onEnable()

        // Load the configuration
        config = PluginConfig.create(dataFolder)
        menuConfig = MenuConfig.create(dataFolder)

        // Enable integrations
        IntegrationManager.enable()

        registerCommands()

        // GUI Event Listener
        Bukkit.getPluginManager().registerEvents(MenuFunctionListener(), this)
    }

    private fun registerCommands() {
        // Business manager
        getCommand("business")!!.apply {
            setExecutor(BusinessManager)
            tabCompleter = BusinessManager
        }

        // Menu commands
        getCommand("guildedmenuadmin")!!.setExecutor(GuiUtil)
        getCommand("guildedmenu")!!.setExecutor(GuiUtil)
    }
}