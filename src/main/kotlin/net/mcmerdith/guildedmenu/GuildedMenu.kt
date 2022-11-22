package net.mcmerdith.guildedmenu

import net.mcmerdith.guildedmenu.business.BusinessManager
import net.mcmerdith.guildedmenu.configuration.MainMenuConfig
import net.mcmerdith.guildedmenu.configuration.PluginConfig
import net.mcmerdith.guildedmenu.gui.business.BusinessLocationMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.SignShopIntegration
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
    lateinit var mainMenuConfig: MainMenuConfig

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
        mainMenuConfig = MainMenuConfig.create(dataFolder)

        // Enable integrations
        IntegrationManager.enable()

        registerCommands()
        registerEvents()
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

    private fun registerEvents() {
        Bukkit.getPluginManager().apply {
            // GUI Event Listener
            registerEvents(MenuFunctionListener(), this@GuildedMenu)

            // Add Business Location
            if (IntegrationManager[SignShopIntegration::class.java]?.ready == true)
                registerEvents(BusinessLocationMenu.EventPlayerInteract(), this@GuildedMenu)
        }
    }
}