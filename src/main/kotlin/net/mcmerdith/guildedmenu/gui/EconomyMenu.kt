package net.mcmerdith.guildedmenu.gui

import org.ipvp.canvas.Menu
import org.ipvp.canvas.type.ChestMenu

class EconomyMenu(parent: Menu?) : BaseMenu(
    "Economy",
    MenuSize(6),
    parent
) {
    class PlayerBalanceMenu(parent: Menu?) : BaseMenu(
        "Player Balance",
        MenuSize(3),
        parent
    )
}