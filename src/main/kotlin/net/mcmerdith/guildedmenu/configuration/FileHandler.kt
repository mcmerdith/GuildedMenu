package net.mcmerdith.guildedmenu.configuration

import java.io.*

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
}