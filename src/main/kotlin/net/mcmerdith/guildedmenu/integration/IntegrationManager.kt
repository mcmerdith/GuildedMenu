package net.mcmerdith.guildedmenu.integration

import net.mcmerdith.guildedmenu.GuildedMenu
import net.mcmerdith.guildedmenu.integration.vault.VaultIntegration

object IntegrationManager {
    private val integrations: MutableMap<Class<out Integration>, Integration> = HashMap()

    @Suppress("MemberVisibilityCanBePrivate")
    fun register(clazz: Class<out Integration>, i: Integration) {
        integrations[clazz] = i
    }

    fun setup() {
        for (i in integrations.values) if (i.pluginInstalled) i.setup()
    }

    fun enable() {
        for (i in integrations.values) {
            if (!i.pluginEnabled) continue

            if (i.onEnable()) {
                i.resetError()
                GuildedMenu.plugin.logger.info("Enabled " + i.pluginName + " integration")
            } else {
                i.setError()
                GuildedMenu.plugin.logger.warning("Failed to enable " + i.pluginName + " integration")
            }
        }
    }

    /**
     * Gets the [integration] instance
     *
     * Returns the integration (or null if no instance is registered)
    </T> */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Integration> get(integration: Class<T>): T? {
        val i = integrations[integration] ?: return null
        return i as T
    }

    init {
        register(EssentialsIntegration::class.java, EssentialsIntegration())
        register(VaultIntegration::class.java, VaultIntegration())
        register(SignShopIntegration::class.java, SignShopIntegration())
        register(TownyIntegration::class.java, TownyIntegration())
        register(MMOItemsIntegration::class.java, MMOItemsIntegration())
        register(MythicMobsIntegration::class.java, MythicMobsIntegration())
    }
}