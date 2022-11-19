package net.mcmerdith.guildedmenu.gui.util

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.Extensions.setName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object ItemTemplates {

    /*
    Items
     */

    val MONEYBLOCK
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg4OWNmY2JhY2JlNTk4ZThhMWNkODYxMGI0OWZjYjYyNjQ0ZThjYmE5ZDQ5MTFkMTIxMTM0NTA2ZDhlYTFiNyJ9fX0=")
    val REGISTER_GREEN
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjE4YmYzNTYwY2M4ZDA0ODI3NDA2NGNkMDE0MjI1N2Y4YjlmMTI3YmMwNDQ0ZmE1NzExOGRlMzJlODhmMzBkYSJ9fX0=")
    val REGISTER_ORANGE
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I2NDhiOWE0NGUyODBiY2RmMjVmNGE2NmE5N2JkNWMzMzU0MmU1ZTgyNDE1ZTE1YjQ3NWM2Yjk5OWI4ZDYzNSJ9fX0=")
    val ERROR
        get() = ItemStack(Material.BARRIER).setName("Internal Error")

    /*
    Menu Icons
     */

    val TPA
        get() = ItemStack(Material.ENDER_PEARL).setName("TPA").setLore("Request to TPA to another player")
    val TPA_HERE
        get() = ItemStack(Material.ENDER_EYE).setName("TPA Here").setLore("Request another player TPA to you")
    val ECONOMY
        get() = MONEYBLOCK.setName("Economy").setLore("Left-Click : Top Server Balances", "Right-Click: Your Account")

    /*
    Controls
    https://minecraft-heads.com/custom-heads/tags/var/Icons%20(Ironblock)
     */

    val PREV_BUTTON
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTk0NTQ5MTg5ODQ5NmIxMzZmZmFmODJlZDM5OGE1NDU2ODI4OWEzMzEwMTVhNjRjODQzYTM5YzBjYmYzNTdmNyJ9fX0=")
            .setName("Previous")
    val NEXT_BUTTON
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFlMjk0MjJkYjQwNDdlZmRiOWJhYzJjZGFlNWEwNzE5ZWI3NzJmY2NjODhhNjZkOTEyMzIwYjM0M2MzNDEifX19")
            .setName("Next")
    val PREV_BUTTON_DISABLED
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGNlYzgwN2RjYzE0MzYzMzRmZDRkYzlhYjM0OTM0MmY2YzUyYzllN2IyYmYzNDY3MTJkYjcyYTBkNmQ3YTQifX19")
            .setName("Previous")
    val NEXT_BUTTON_DISABLED
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAxYzdiNTcyNjE3ODk3NGIzYjNhMDFiNDJhNTkwZTU0MzY2MDI2ZmQ0MzgwOGYyYTc4NzY0ODg0M2E3ZjVhIn19fQ==")
            .setName("Next")
//    val BACK_BUTTON
//        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjdmMDQ2ZjFjNWY4NTQwNzM1ZDQzNmU2NjQzYjM3YTkxNjUwNmYxYjllMzM3OTkzNTFlM2MzOWYwODI5YzJhYiJ9fX0=")
//            .name("Back")
}