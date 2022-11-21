package net.mcmerdith.guildedmenu.gui.framework

import org.bukkit.entity.Player
import java.util.function.Supplier

/**
 * Called when [Player] selects something
 *
 * Return true to close the inventory
 */
typealias MenuSelectReceiver<T> = (Player, T) -> Boolean
typealias MenuProvider = Supplier<MenuBase>