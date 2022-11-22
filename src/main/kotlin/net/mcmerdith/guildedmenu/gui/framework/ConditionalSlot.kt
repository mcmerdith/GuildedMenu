package net.mcmerdith.guildedmenu.gui.framework

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.slot.Slot.ClickHandler
import org.ipvp.canvas.slot.SlotSettings
import org.ipvp.canvas.template.ItemStackTemplate

/**
 * Slot that displays conditionally
 */
object ConditionalSlot {

    /**
     * When [condition] is met render [item]
     */
    class ConditionalItemTemplate(
        private val item: ItemStack,
        private val condition: (Player) -> Boolean
    ) : ItemStackTemplate {
        override fun getItem(player: Player?): ItemStack? {
            player ?: return null

            return if (condition.invoke(player)) item
            else null
        }
    }

    /**
     * When [condition] is met render [item] with a
     * [ConditionalClickHandler] ([click], [rightClick])
     */
    fun build(
        item: ItemStack,
        condition: (Player) -> Boolean,
        click: ClickHandler? = null,
        rightClick: ClickHandler? = null
    ): SlotSettings = SlotSettings.builder()
        .itemTemplate(ConditionalItemTemplate(item, condition))
        .clickHandler(ConditionalClickHandler(condition, click, rightClick))
        .build()
}