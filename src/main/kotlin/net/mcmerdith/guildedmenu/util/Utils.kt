package net.mcmerdith.guildedmenu.util

import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import org.bukkit.entity.Player
import org.ipvp.canvas.slot.Slot.ClickHandler
import java.util.function.Supplier

/**
 * Called when [Player] selects something
 *
 * Return true to close the inventory
 */
typealias MenuSelectReceiver<T> = (Player, T) -> Boolean
typealias MenuProvider = Supplier<BaseMenu>
typealias Filter<T> = (T) -> Boolean

/**
 * Returns this string with the first letter capitalized
 */
fun String.capitalize(): String {
    return if (isEmpty()) this
    else "${substring(0, 1).uppercase()}${if (length > 1) substring(1) else ""}"
}

fun <T> MenuSelectReceiver<T>.getHandler(selected: T): ClickHandler {
    return ClickHandler { player, _ ->
        if (this.invoke(player, selected)) player.closeInventory()
    }
}