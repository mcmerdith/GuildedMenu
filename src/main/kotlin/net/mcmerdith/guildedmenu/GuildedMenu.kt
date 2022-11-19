package net.mcmerdith.guildedmenu

import net.mcmerdith.guildedmenu.business.BusinessManager
import net.mcmerdith.guildedmenu.configuration.MenuConfig
import net.mcmerdith.guildedmenu.configuration.PluginConfig
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.util.GMLogger
import net.mcmerdith.guildedmenu.util.Globals
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
        GMLogger.init(logger)

        Globals.init(this)
        IntegrationManager.setup()
        BusinessManager.init()

        super.onLoad()
    }

    override fun onEnable() {
        super.onEnable()

        // Load the configuration
        config = PluginConfig.create(dataFolder)
        menuConfig = MenuConfig.create(dataFolder)

        // Set up our integrations
        IntegrationManager.enable()

        // Business manager
        getCommand("business")!!.setExecutor(BusinessManager)
        getCommand("guildedmenu")!!.setExecutor(GuiUtil)

        // GUI
        Bukkit.getPluginManager().registerEvents(MenuFunctionListener(), this)
    }
}