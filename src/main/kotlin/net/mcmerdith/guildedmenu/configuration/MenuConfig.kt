package net.mcmerdith.guildedmenu.configuration

import de.exlll.configlib.*
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import java.io.File

class MenuConfig : BaseConfiguration() {
    @Configuration
    class AdminMenuConfig {
        @Comment("Icon to switch between admin and player menu")
        var mainButton = MenuIcon(true, 1, 1)
    }

    companion object {
        fun create(dataFolder: File) = Factory("menu.yml", MenuConfig::class.java)
            .header(
                """
                    ######## Main Menu ########                    
                    - Row   : 1-6
                    - Column: 1-9
                """.trimIndent()
            ).create(dataFolder)
    }

    @JvmRecord
    data class MenuIcon(val enabled: Boolean = true, val row: Int, val col: Int) {
        val index get() = GuiUtil.getSlotIndex(row, col)
    }

    @Comment("Menu Title (can use colors such as §0)", "https://htmlcolorcodes.com/minecraft-color-codes/")
    var title = "GuildedCraft Menu"

    @Comment("Vault integration", "BalTop, Pay Player, etc")
    var vault = MenuIcon(true, 1, 5)

    @Comment("Requires EssentialsX")
    var tpa = MenuIcon(true, 2, 3)

    @Comment("Requires EssentialsX")
    var tpaHere = MenuIcon(true, 2, 7)

    @Comment("Towny integration")
    var towny = MenuIcon(true, 2, 7)

    @Comment("Signshop integration", "Also: /business")
    var signshop = MenuIcon(true, 2, 7)

    @Comment("Admin menu options")
    var admin = AdminMenuConfig()
}