package net.mcmerdith.guildedmenu.util

import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import java.util.*

@Suppress("unused")
object ItemStackUtils {
    /**
     * Set the item display name to [name]
     */
    fun ItemStack.setName(name: String? = null): ItemStack {
        val meta = itemMeta ?: run {
            GMLogger.MAIN.warn("Attempted to set name for ${type.name}x$amount, which has no meta!")
            return this
        }

        // Blank will use default item name
        if (name.isNullOrBlank()) meta.setDisplayName(ChatColor.RESET.toString())
        else meta.setDisplayName(name)

        itemMeta = meta

        return this
    }

    /**
     * Get the item name
     */
    fun ItemStack.getName() = itemMeta?.displayName

    /**
     * Add [lore] to the item lore
     */
    fun ItemStack.addLore(vararg lore: String): ItemStack {
        val meta = itemMeta ?: run {
            GMLogger.MAIN.warn("Attempted to add lore to ${type.name}x$amount, which has no meta!")
            return this
        }

        return if (meta.lore == null) {
            setLore(*lore)
        } else {
            meta.lore!!.addAll(lore)
            itemMeta = meta

            this
        }
    }

    /**
     * Set the lore to [lore]
     */
    fun ItemStack.setLore(vararg lore: String): ItemStack {
        val meta = itemMeta ?: run {
            GMLogger.MAIN.warn("Attempted to set lore on ${type.name}x$amount, which has no meta!")
            return this
        }

        meta.lore = mutableListOf(*lore)

        itemMeta = meta

        return this
    }

    /**
     * Get the item lore
     */
    fun ItemStack.getLore() = itemMeta?.lore ?: emptyList()
}