package net.mcmerdith.guildedmenu.business

import net.mcmerdith.guildedmenu.util.ItemStackUtils.getName
import net.mcmerdith.guildedmenu.util.toSentenceCase
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class BusinessLocation private constructor() {
    var icon: Material? = null
    var world: String? = null
    var x: Int? = null
    var y: Int? = null
    var z: Int? = null
    var description: String? = null
    var items = mutableListOf<String>()

    constructor(icon: Material, location: Location, description: String, items: List<ItemStack>) : this() {
        this.icon = icon
        this.world = location.world?.name
        this.x = location.blockX
        this.y = location.blockY
        this.z = location.blockZ

        this.description = description.toSentenceCase()
        this.items.addAll(items.map {
            "${it.getName() ?: it.type.name}x${it.amount}"
        })
    }

    fun isSimilar(location: BusinessLocation): Boolean {
        return world == location.world && x == location.x && y == location.y && z == location.z
    }
}