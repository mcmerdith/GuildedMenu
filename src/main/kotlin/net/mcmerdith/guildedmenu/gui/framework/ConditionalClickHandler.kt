package net.mcmerdith.guildedmenu.gui.framework

import org.bukkit.entity.Player
import org.ipvp.canvas.ClickInformation
import org.ipvp.canvas.slot.Slot

/**
 * When clicked and [condition] is met:
 *
 * Execute [click] (if provided)
 * or [rightClick] if provided and the player right-clicked
 */
class ConditionalClickHandler(
    private val condition: (Player) -> Boolean,
    private val click: Slot.ClickHandler? = null,
    private val rightClick: Slot.ClickHandler? = null
) : Slot.ClickHandler {
    override fun click(player: Player, clickInfo: ClickInformation) {
        if (condition.invoke(player)) {
            if (rightClick != null && clickInfo.clickType.isRightClick) {
                rightClick.click(player, clickInfo)
            } else {
                click?.click(player, clickInfo)
            }
        }
    }
}