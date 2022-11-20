package net.mcmerdith.guildedmenu.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

object Globals {
    @Suppress("unused")
    val DEBUG_PLAYER: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(UUID.fromString("a8ae1005-73e3-49ba-b94e-bbf5143451bb"))

    object PERMISSION {
        const val ADMIN = "guildedmenu.admin"
    }
}