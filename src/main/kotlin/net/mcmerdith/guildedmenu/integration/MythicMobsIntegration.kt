package net.mcmerdith.guildedmenu.integration

import io.lumine.mythic.api.mobs.MythicMob
import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.Location

class MythicMobsIntegration : Integration("MythicMobs") {
    override fun onEnable(): Boolean {
        return true
    }

    private val api by lazy { MythicBukkit.inst() }

    fun allMobs(): Collection<MythicMob> = api.mobManager.mobTypes

    fun spawnMob(mob: MythicMob, location: Location, level: Int = 1) {
        try {
            api.apiHelper.spawnMythicMob(mob, location, level)
        } catch (_: Exception) {
            // mythic be like, null-check? more like nah-check
        }
    }
}