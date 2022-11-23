package net.mcmerdith.guildedmenu.gui.mythic

import dev.dbassett.skullcreator.SkullCreator
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.MythicMobsIntegration
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.MenuProvider
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.SlotSettings

class MythicMobSelectMenu(private val previous: MenuProvider?) : PaginatedMenu() {
    private val mythicmobs = IntegrationManager[MythicMobsIntegration::class.java]!!

    override fun getBuilder() =
        BaseMenu.Builder(6).title("Mythic Mob Spawner").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(5)

    override fun setup(builder: PaginatedMenuBuilder) {
        for (mob in mythicmobs.allMobs()) {
            builder.addItem(
                SlotSettings.builder()
                    .item(SkullCreator.createSkull().setName(mob.internalName))
                    .clickHandler { player, _ ->
                        mythicmobs.spawnMob(mob, player.location)
                    }.build()
            )
        }
    }
}