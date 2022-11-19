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

        /**
         * Set the file header comment
         */
        fun header(header: String?): Factory<T> {
            this.header = header
            return this
        }

        /**
         * Create the config and load data from disk if exists
         */
        fun create(dataFolder: File): T {
            // Get a reference to the file
            val dataFile = File(dataFolder, filename)

            // Set the properties
            val properties = YamlConfigurationProperties.newBuilder()
                .setNameFormatter(NameFormatters.LOWER_UNDERSCORE)
                .header(header).build()

            // Create the config and read data from disk
            val config = YamlConfigurations.update(
                dataFile.toPath(),
                type,
                properties
            )

            // Provide the new config with a reference to its datafile
            config.setFile(dataFile)

            return config
        }
    }

    @Ignore
    private lateinit var dataFile: File

    /**
     * Set the file associated with this config to [dataFile]
     */
    private fun setFile(dataFile: File) {
        this.dataFile = dataFile
    }

    /**
     * Save the config to disk
     */
    fun save() {
        YamlConfigurations.save(this.dataFile.toPath(), this.javaClass, this)
    }
}