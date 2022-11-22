package net.mcmerdith.guildedmenu.gui.business

import net.mcmerdith.guildedmenu.business.Business
import net.mcmerdith.guildedmenu.business.BusinessLocation
import net.mcmerdith.guildedmenu.gui.framework.BaseMenu
import net.mcmerdith.guildedmenu.gui.framework.ConditionalSlot
import net.mcmerdith.guildedmenu.gui.framework.PaginatedMenu
import net.mcmerdith.guildedmenu.gui.util.GuiUtil
import net.mcmerdith.guildedmenu.gui.util.ItemTemplates
import net.mcmerdith.guildedmenu.integration.IntegrationManager
import net.mcmerdith.guildedmenu.integration.SignShopIntegration
import net.mcmerdith.guildedmenu.util.ChatUtils.sendErrorMessage
import net.mcmerdith.guildedmenu.util.ChatUtils.sendSuccessMessage
import net.mcmerdith.guildedmenu.util.ItemStackUtils.addLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.getBusiness
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setBusiness
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setLore
import net.mcmerdith.guildedmenu.util.ItemStackUtils.setName
import net.mcmerdith.guildedmenu.util.MenuProvider
import net.mcmerdith.guildedmenu.util.MenuSelectReceiver
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.ipvp.canvas.paginate.PaginatedMenuBuilder
import org.ipvp.canvas.slot.ClickOptions
import org.ipvp.canvas.slot.SlotSettings

class BusinessLocationMenu(
    private val previous: MenuProvider?,
    private val business: Business,
    private val delete: Boolean = false,
    private val selectReceiver: MenuSelectReceiver<BusinessLocation>? = null
) : PaginatedMenu() {
    override fun getBuilder() =
        BaseMenu.Builder(4).title("Locations (${business.name})").redraw(true).previous(previous)

    override fun getRowMask() = GuiUtil.getFullRowMask(3)

    override fun setup(builder: PaginatedMenuBuilder) {
        builder.apply {
            business.locations.forEach { location ->
                addItem(
                    SlotSettings.builder().item(
                        ItemStack(location.icon ?: Material.OAK_SIGN).apply {
                            setName(location.description)
                            setLore(location.items)

                            if (delete) addLore("${ChatColor.RED}Remove this location")
                        }
                    ).clickHandler { clickPlayer, _ ->
                        // Execute the callback when clicked
                        if (selectReceiver?.invoke(clickPlayer, location) == true) clickPlayer.closeInventory()
                    }.build()
                )
            }

            if (delete) return

            newMenuModifier { menu ->
                menu.getSlot(4, 4).settings = ConditionalSlot.build(
                    ItemTemplates.UI.getNew("Add locations"),
                    { p -> business.isOwner(p) },
                    { p, _ ->
                        BaseMenu.Builder(1)
                            .title("Add a location")
                            .build()
                            .apply {
                                getSlot(1, 5).apply {
                                    item = ItemTemplates.getEnchantedStick("Add location")
                                        .setLore(
                                            "Add a SignShop sign to '${business.name}'",
                                            "Sneak+Right-Click to dispose of this item"
                                        )
                                        .setBusiness(business)

                                    clickOptions = ClickOptions.ALLOW_ALL
                                }
                            }.open(p)
                    }
                )

                menu.getSlot(4, 6).settings = ConditionalSlot.build(
                    ItemTemplates.UI.getDelete("Remove a location"),
                    { p -> business.isOwner(p) },
                    { p, _ ->
                        BusinessLocationMenu(
                            this@BusinessLocationMenu,
                            business,
                            true
                        ) { _, location ->
                            business.locations.remove(location)
                            business.save()
                            true
                        }.get().open(p)
                    }
                )
            }
        }
    }

    class EventPlayerInteract : Listener {
        @EventHandler
        fun onPlayerInteract(event: PlayerInteractEvent) {
            val business = event.item?.getBusiness() ?: return

            // If this is a business item cancel the event
            event.isCancelled = true

            if (
                event.player.isSneaking &&
                (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)
            ) {
                // Remove the item if sneak right click
                event.player.inventory.remove(event.item!!)
                event.player.updateInventory()
                return
            }

            val block = event.clickedBlock ?: return

            val signshop = IntegrationManager[SignShopIntegration::class.java] ?: return

            if (!signshop.ready) return

            val shop = signshop.getShop(block.location) ?: run {
                event.player.sendErrorMessage("That is not a shop!")
                return
            }

            // TODO AnvilGUI name

            val location = BusinessLocation(
                shop.items.firstOrNull()?.type ?: Material.OAK_SIGN,
                block.location,
                shop.operation,
                listOf(*shop.items)
            )

            if (business.addLocation(location)) {
                event.player.sendSuccessMessage("Added shop to '${business.name}'")
            } else {
                event.player.sendErrorMessage("This shop is already registered to '${business.name}'")
            }
        }
    }
}