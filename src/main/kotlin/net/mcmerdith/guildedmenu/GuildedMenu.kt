package net.mcmerdith.guildedmenu

import net.mcmerdith.guildedmenu.components.BusinessManager
import net.mcmerdith.guildedmenu.configuration.PluginConfig
import net.mcmerdith.guildedmenu.gui.GuiUtil
import net.mcmerdith.guildedmenu.util.Globals
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

    lateinit var configuration: PluginConfig

    override fun onLoad() {
        GMLogger.init(logger)

        Globals.init(this);
        IntegrationManager.setup()
        BusinessManager.init()

        super.onLoad()
    }

    override fun onEnable() {
        super.onEnable()

        // Load the configuration
        configuration = PluginConfig.getNewConfig(dataFolder)

        // Set up our integrations
        IntegrationManager.enable()

        // Business manager
        getCommand("business")!!.setExecutor(BusinessManager)
        getCommand("guildedmenu")!!.setExecutor(GuiUtil)

        // GUI
        Bukkit.getPluginManager().registerEvents(MenuFunctionListener(), this)
    }
}