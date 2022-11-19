package net.mcmerdith.guildedmenu.configuration

import de.exlll.configlib.*
import java.io.File

class PluginConfig : BaseConfiguration() {
    companion object {
        fun create(dataFolder: File) = Factory("config.yml", PluginConfig::class.java)
            .header(
                "######## Guilded Menu ########".trimIndent()
            ).create(dataFolder)
    }

    @Comment("Enable debug logging")
    var debug = false

    @Comment("Log the built configuration file for constructed panels")
    var logBuiltPanels = false
}