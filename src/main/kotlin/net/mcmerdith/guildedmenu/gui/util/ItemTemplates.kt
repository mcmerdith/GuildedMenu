package net.mcmerdith.guildedmenu.gui.util

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.util.Extensions.setLore
import net.mcmerdith.guildedmenu.util.Extensions.setName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Suppress("unused")
object ItemTemplates {

    /*
    Items
     */

    val MONEYBLOCK: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg4OWNmY2JhY2JlNTk4ZThhMWNkODYxMGI0OWZjYjYyNjQ0ZThjYmE5ZDQ5MTFkMTIxMTM0NTA2ZDhlYTFiNyJ9fX0=")
    val REGISTER_GREEN: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjE4YmYzNTYwY2M4ZDA0ODI3NDA2NGNkMDE0MjI1N2Y4YjlmMTI3YmMwNDQ0ZmE1NzExOGRlMzJlODhmMzBkYSJ9fX0=")
    val REGISTER_ORANGE: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I2NDhiOWE0NGUyODBiY2RmMjVmNGE2NmE5N2JkNWMzMzU0MmU1ZTgyNDE1ZTE1YjQ3NWM2Yjk5OWI4ZDYzNSJ9fX0=")
    val ERROR: ItemStack
        get() = ItemStack(Material.BARRIER).setName("Internal Error")

    /*
    Menu Icons
    https://minecraft-heads.com/custom-heads/tags/var/Icons%20(Other)
     */

    val HOME: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTJkN2E3NTFlYjA3MWUwOGRiYmM5NWJjNWQ5ZDY2ZTVmNTFkYzY3MTI2NDBhZDJkZmEwM2RlZmJiNjhhN2YzYSJ9fX0=")
            .setName("Home")
    val CHECKMARK: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=")
    val QUESTIONMARK: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=")
    val XMARK: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=")
    val EXCLAMATION: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZlNTIyZDkxODI1MjE0OWU2ZWRlMmVkZjNmZTBmMmMyYzU4ZmVlNmFjMTFjYjg4YzYxNzIwNzIxOGFlNDU5NSJ9fX0=")
    val INFO: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDAxYWZlOTczYzU0ODJmZGM3MWU2YWExMDY5ODgzM2M3OWM0MzdmMjEzMDhlYTlhMWEwOTU3NDZlYzI3NGEwZiJ9fX0=")
            .setName("More Information")
    val EDIT: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTdlZDY2ZjVhNzAyMDlkODIxMTY3ZDE1NmZkYmMwY2EzYmYxMWFkNTRlZDVkODZlNzVjMjY1ZjdlNTAyOWVjMSJ9fX0=")
            .setName("Edit")
    val NEW: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThjZjJjMmI3NWI5NzM0MzkwMWY2N2VjMGVmYmNmYzBmNDkzMzVlYmFjNDQwNGUyN2NmMjVhNzlkYmQyMTU2MSJ9fX0=")
            .setName("New")
    val DELETE: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNkN2E5ZWUzMTM0OGEzNTc1NDM4M2MxNjdmYTMzYWJjMDJlOGU2OGNhMmM0YTk2OTE0MDBlN2ZlMzRiM2ViNSJ9fX0=")
            .setName("Delete")
    val REFRESH: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUzZGQ0NTc5ZWRjMmE2ZjIwMzJmOTViMWMxODk4MTI5MWI2YzdjMTFlYjM0YjZhOGVkMzZhZmJmYmNlZmZmYiJ9fX0=")
            .setName("Delete")
    val TRANSFER: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2RmNTM2YzNkMDVmN2ZjODMyNTAxYjRjMTE1YWNlZmE0M2E2NTIwOWMzZDU2NGM2YzU2Zjk0OTc5NmYxMDI5YSJ9fX0=")
            .setName("Delete")
    val TPA: ItemStack
        get() = ItemStack(Material.ENDER_PEARL).setName("TPA").setLore("Request to TPA to another player")
    val TPA_HERE: ItemStack
        get() = ItemStack(Material.ENDER_EYE).setName("TPA Here").setLore("Request another player TPA to you")
    val ECONOMY: ItemStack
        get() = MONEYBLOCK.setName("Economy").setLore("Left-Click : Top Server Balances", "Right-Click: Your Account")

    /*
    Controls
    https://minecraft-heads.com/custom-heads/tags/var/Icons%20(Ironblock)
     */

    val PREV_BUTTON: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTk0NTQ5MTg5ODQ5NmIxMzZmZmFmODJlZDM5OGE1NDU2ODI4OWEzMzEwMTVhNjRjODQzYTM5YzBjYmYzNTdmNyJ9fX0=")
            .setName("Previous")
    val NEXT_BUTTON: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFlMjk0MjJkYjQwNDdlZmRiOWJhYzJjZGFlNWEwNzE5ZWI3NzJmY2NjODhhNjZkOTEyMzIwYjM0M2MzNDEifX19")
            .setName("Next")
    val PREV_BUTTON_DISABLED: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGNlYzgwN2RjYzE0MzYzMzRmZDRkYzlhYjM0OTM0MmY2YzUyYzllN2IyYmYzNDY3MTJkYjcyYTBkNmQ3YTQifX19")
            .setName("Previous")
    val NEXT_BUTTON_DISABLED: ItemStack
        get() = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAxYzdiNTcyNjE3ODk3NGIzYjNhMDFiNDJhNTkwZTU0MzY2MDI2ZmQ0MzgwOGYyYTc4NzY0ODg0M2E3ZjVhIn19fQ==")
            .setName("Next")
}