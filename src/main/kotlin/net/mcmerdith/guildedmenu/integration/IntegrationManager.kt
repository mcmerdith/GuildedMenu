package net.mcmerdith.guildedmenu.integration

import net.mcmerdith.guildedmenu.GuildedMenu

object IntegrationManager {
    private val integrations: MutableMap<Class<out Integration>, Integration> = HashMap()

    fun setup() {
        for (i in integrations.values) i.setup()
    }

    fun enable() {
        for (i in integrations.values) {
            if (i.isAvailable) {
                if (i.onEnable()) {
                    GuildedMenu.plugin.logger.info("Loaded " + i.pluginName + " integration")
                } else {
                    GuildedMenu.plugin.logger.warning("Failed to load " + i.pluginName + " integration")
                }
            }
        }
    }

    private fun register(clazz: Class<out Integration>, i: Integration) {
        integrations[clazz] = i
    }

    /**
     * Gets an integration
     *
     * @param clazz The integration to retrieve
     * @param <T>   something...
     * @return The integration, if available, otherwise null
    </T> */
    operator fun <T : Integration?> get(clazz: Class<T>): T? {
        val i = integrations[clazz] ?: return null
        return i as T
    }

    fun has(clazz: Class<out Integration>): Boolean {
        return if (integrations.containsKey(clazz)) {
            integrations[clazz]!!.isAvailable
        } else false
    }

    init {
        register(SignShopIntegration::class.java, SignShopIntegration())
        register(VaultIntegration::class.java, VaultIntegration())
        register(EssentialsIntegration::class.java, EssentialsIntegration())
    }
}