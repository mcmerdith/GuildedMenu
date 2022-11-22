package net.mcmerdith.guildedmenu.util

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import org.bukkit.entity.Player
import java.util.function.Supplier

/**
 * Called when [Player] selects something
 *
 * Return true to close the inventory
 */
typealias MenuSelectReceiver<T> = (Player, T) -> Boolean
typealias MenuProvider = Supplier<BaseMenu>

fun String.toSentenceCase(): String {
    return if (isEmpty()) this
    else "${substring(0, 1).uppercase()}${if (length > 1) substring(1) else ""}"
}