package net.mcmerdith.guildedmenu.integration

import net.Indyuce.mmoitems.MMOItems
import net.Indyuce.mmoitems.api.Type
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MMOItemsIntegration : Integration("MMOItems") {
    override fun onEnable(): Boolean {
        return true
    }

    private val api by lazy { MMOItems.plugin }

    private val templates = api.templates

    val types: Collection<Type>
        get() = api.types.all

    fun getTypeIcon(type: Type): ItemStack {
        return (getItems(type).firstOrNull()?.newBuilder()?.build()?.newBuilder()?.build()
            ?: ItemStack(if (type.isWeapon) Material.IRON_SWORD else Material.STONE)).setName(type.name).setLore()
    }

    fun getAllItems() {
        val templates = mutableListOf<MMOItemTemplate>()
        types.forEach { type ->
            templates.addAll(getItems(type))
        }
    }

    fun getItems(type: Type): Collection<MMOItemTemplate> = templates.getTemplates(type)
}