package net.mcmerdith.guildedmenu.gui

import org.ipvp.canvas.Menu
import org.ipvp.canvas.type.ChestMenu

class EconomyMenu(parent: Menu?) : ChestMenu(
    "Economy",
    MenuSize(6).area,
    parent,
    true
) {
    class PlayerBalanceMenu(parent: Menu?) : ChestMenu(
        "Player Balance",
        MenuSize(3).area,
        parent,
        true
    )
}