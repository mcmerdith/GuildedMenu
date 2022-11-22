package net.mcmerdith.guildedmenu.gui.framework

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.ClickInformation
import org.ipvp.canvas.slot.Slot.ClickHandler
import org.ipvp.canvas.slot.SlotSettings
import org.ipvp.canvas.template.ItemStackTemplate

object ConditionalSlot {
    class ConditionalClickHandler(
        private val condition: (Player) -> Boolean,
        private val click: ClickHandler? = null,
        private val rightClick: ClickHandler? = null
    ) : ClickHandler {
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