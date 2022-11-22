package net.mcmerdith.guildedmenu.util

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.business.BusinessManager
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
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
    fun ItemStack.getName(): String? {
        val name = itemMeta?.displayName

        return if (name.isNullOrBlank()) null
        else name
    }

    /**
     * Add [lore] to the item lore
     */
    fun ItemStack.addLore(vararg lore: String) = addLore(listOf(*lore))

    /**
     * Add [lore] to the item lore
     */
    fun ItemStack.addLore(lore: List<String>): ItemStack {
        val meta = itemMeta ?: run {
            GMLogger.MAIN.warn("Attempted to add lore to ${type.name}x$amount, which has no meta!")
            return this
        }

        return if (meta.lore == null) {
            setLore(lore)
        } else {
            setLore(ArrayList(meta.lore!!).apply { addAll(lore) })
        }
    }

    /**
     * Set the lore to [lore]
     */
    fun ItemStack.setLore(vararg lore: String) = setLore(listOf(*lore))

    /**
     * Set the lore to [lore]
     */
    fun ItemStack.setLore(lore: List<String>): ItemStack {
        val meta = itemMeta ?: run {
            GMLogger.MAIN.warn("Attempted to set lore on ${type.name}x$amount, which has no meta!")
            return this
        }

        meta.lore = ArrayList(lore.map { line ->
            if (line.startsWith(ChatColor.RESET.toString())) line
            else "${ChatColor.RESET}$line"
        })

        itemMeta = meta

        return this
    }

    /**
     * Get the item lore
     */
    fun ItemStack.getLore() = itemMeta?.lore ?: emptyList()

    private fun getBusinessIdentifierKey() = NamespacedKey(GuildedMenu.plugin, "business-id")

    fun ItemStack.setBusiness(business: Business): ItemStack {
        val meta = itemMeta ?: return this

        meta.persistentDataContainer.set(getBusinessIdentifierKey(), UUIDTagType.TYPE, business.id!!)

        itemMeta = meta

        return this
    }

    fun ItemStack.getBusiness(): Business? {
        val container = itemMeta?.persistentDataContainer ?: return null

        val uuid = container.get(getBusinessIdentifierKey(), UUIDTagType.TYPE) ?: return null

        return BusinessManager.find(uuid)
    }
}