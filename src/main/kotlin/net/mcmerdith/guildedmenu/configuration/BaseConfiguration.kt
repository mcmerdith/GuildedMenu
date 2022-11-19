package net.mcmerdith.guildedmenu.configuration

import de.exlll.configlib.*
import java.io.File

@Configuration
abstract class BaseConfiguration {
    protected class Factory<T : BaseConfiguration>(
        private val filename: String,
        private val type: Class<T>
    ) {
        var header: String? = null

        fun header(header: String?): Factory<T> {
            this.header = header
            return this
        }

        fun create(dataFolder: File): T {
            val dataFile = File(dataFolder, filename)

            val properties = YamlConfigurationProperties.newBuilder()
                .setNameFormatter(NameFormatters.LOWER_UNDERSCORE)
                .header(header).build()

            val config = YamlConfigurations.update(
                dataFile.toPath(),
                type,
                properties
            )

            config.setFile(dataFile)

            return config
        }
    }

    @Ignore
    private lateinit var dataFile: File

    private fun setFile(dataFile: File) {
        this.dataFile = dataFile
    }

    fun save() {
        YamlConfigurations.save(this.dataFile.toPath(), this.javaClass, this)
    }
}