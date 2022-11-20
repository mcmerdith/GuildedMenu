package net.mcmerdith.guildedmenu.business

import net.mcmerdith.guildedmenu.GuildedMenu
import java.io.*
import java.util.*
import java.util.stream.Collectors

object FileHandler {
    @Throws(IOException::class)
    fun writeDataFile(json: String, file: File) {
        val writer = FileWriter(file)
        writer.write(json)
        writer.flush()
        writer.close()
    }

    @Throws(IOException::class)
    fun readDataFile(file: File): String {
        val reader = BufferedReader(FileReader(file))
        val data = reader.readLine()
        reader.close()
        return data
    }

    /**
     * The directory where business data files are stored
     */
    private val businessConfigDir: File by lazy {
        File(GuildedMenu.plugin.dataFolder, "business" + File.separator).apply {
            if (!exists()) mkdir()
        }
    }

    fun getBusinessConfigFile(id: UUID) = File(businessConfigDir, "$id.json")

    /**
     * Get a list of all business IDs
     */
    val allBusinessIds: List<String>
        get() {
            val filter = FilenameFilter { _: File?, name: String -> name.endsWith(".json") }
            val files = businessConfigDir.listFiles(filter) ?: return ArrayList()

            return Arrays.stream(files).map { file: File -> file.name.substring(0, file.name.length - 5) }
                .collect(Collectors.toList())
        }
}