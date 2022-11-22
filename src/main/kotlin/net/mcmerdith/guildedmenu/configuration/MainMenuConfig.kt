package net.mcmerdith.guildedmenu.configuration

import de.exlll.configlib.*
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import java.io.File

class MainMenuConfig : BaseConfiguration() {
    @Configuration
    class AdminMenuConfig {
        @Comment("Icon to switch between admin and player menu")
        var mainButton = MenuIcon(true, 1, 1)
    }

    companion object {
        fun create(dataFolder: File) = Factory("menu.yml", MainMenuConfig::class.java)
            .header(
                "######## Main Menu ########\n- Row   : 1-5\n- Column: 1-9".trimIndent()
            ).create(dataFolder)
    }

    @JvmRecord
    data class MenuIcon(val enabled: Boolean = true, val row: Int, val col: Int) {
        val index get() = GuiUtil.getSlotIndex(row, col)
    }

    @Comment("Menu Title (can use colors such as §0)", "https://htmlcolorcodes.com/minecraft-color-codes/")
    var title = "GuildedCraft Menu"

    @Comment("Displays info about the plugin", "Also: button to switch to admin view")
    var info = MenuIcon(true, 1, 1)

    @Comment("Vault integration", "BalTop, Pay Player, etc")
    var vault = MenuIcon(true, 1, 5)

    @Comment("Requires EssentialsX")
    var afk = MenuIcon(true, 1, 8)

    @Comment("Player Profile")
    var profile = MenuIcon(true, 1, 9)

    @Comment("Requires EssentialsX")
    var suicide = MenuIcon(true, 2, 9)

    @Comment("Teleport Random")
    var tpr = MenuIcon(true, 3, 2)

    @Comment("Requires EssentialsX")
    var tpa = MenuIcon(true, 2, 3)

    @Comment("Requires EssentialsX")
    var back = MenuIcon(true, 3, 3)

    @Comment("Requires EssentialsX")
    var tpaHere = MenuIcon(true, 4, 3)

    @Comment("Signshop integration", "Also: /business")
    var signshop = MenuIcon(true, 3, 5)

    @Comment("Requires EssentialsX")
    var home = MenuIcon(true, 5, 7)

    @Comment("Towny integration")
    var towny = MenuIcon(true, 5, 5)

    @Comment("Admin menu options")
    var admin = AdminMenuConfig()
}