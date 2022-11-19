package net.mcmerdith.guildedmenu.gui.util

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.util.Extensions.name
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object ItemTemplates {
    val ERROR
        get() = ItemStack(Material.BARRIER).name("Internal Error")
    val TPA
        get() = ItemStack(Material.ENDER_PEARL).name("TPA")
    val TPA_HERE
        get() = ItemStack(Material.ENDER_EYE).name("TPA Here")
    val MONEYBLOCK
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg4OWNmY2JhY2JlNTk4ZThhMWNkODYxMGI0OWZjYjYyNjQ0ZThjYmE5ZDQ5MTFkMTIxMTM0NTA2ZDhlYTFiNyJ9fX0=")
    val ECONOMY
        get() = MONEYBLOCK.name("Economy")
    val PREV_BUTTON
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGNlYzgwN2RjYzE0MzYzMzRmZDRkYzlhYjM0OTM0MmY2YzUyYzllN2IyYmYzNDY3MTJkYjcyYTBkNmQ3YTQifX19")
            .name("Previous")
    val NEXT_BUTTON
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAxYzdiNTcyNjE3ODk3NGIzYjNhMDFiNDJhNTkwZTU0MzY2MDI2ZmQ0MzgwOGYyYTc4NzY0ODg0M2E3ZjVhIn19fQ==")
            .name("Next")
    val BACK_BUTTON
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjdmMDQ2ZjFjNWY4NTQwNzM1ZDQzNmU2NjQzYjM3YTkxNjUwNmYxYjllMzM3OTkzNTFlM2MzOWYwODI5YzJhYiJ9fX0=")
            .name("Back")
}