package net.mcmerdith.guildedmenu.configuration

import de.exlll.configlib.*
import lombok.Getter
import java.io.File

@Getter
@Configuration
class PluginConfig {
    companion object {
        fun getNewConfig(dataFolder: File): PluginConfig {
            val dataFile = File(dataFolder, "config.yml")

            val properties = YamlConfigurationProperties.newBuilder()
                .setNameFormatter(NameFormatters.LOWER_UNDERSCORE)
                .header(
                    """
                    ######## Guilded Menu ########
                           v0.1 - mcmerdith
                    
                """.trimIndent()
                ).build()

            val config = YamlConfigurations.update(
                dataFile.toPath(),
                PluginConfig::class.java,
                properties
            )

            config.setFile(dataFile)

            return config
        }
    }

    @Ignore
    private lateinit var dataFile: File

    @Comment("Enable debug logging")
    var debug = false

    @Comment("Log the built configuration file for constructed panels")
    var logBuiltPanels = false

    private fun setFile(dataFile: File) {
        this.dataFile = dataFile
    }

    fun save() {
        YamlConfigurations.save(this.dataFile.toPath(), PluginConfig::class.java, this)
    }
}