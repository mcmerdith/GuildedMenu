package net.mcmerdith.guildedmenu.integration

import org.bukkit.Location
import org.wargamer2010.signshop.Seller
import org.wargamer2010.signshop.configuration.Storage

class SignShopIntegration : Integration("SignShop") {
    override fun onEnable(): Boolean {
        return true
    }

    private val api by lazy { Storage.get() }

    fun getShop(location: Location): Seller? = api.getSeller(location)
}