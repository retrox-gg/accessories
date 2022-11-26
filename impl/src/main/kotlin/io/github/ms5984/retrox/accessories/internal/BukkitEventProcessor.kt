package io.github.ms5984.retrox.accessories.internal
/*
 *  Copyright 2022 ms5984, Retrox
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import io.github.ms5984.retrox.accessories.api.AccessoryService
import io.github.ms5984.retrox.accessories.model.SpecialAction
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

class BukkitEventProcessor(private val plugin: AccessoriesPlugin): Listener {
    // Prepare accessory slots on join
    @EventHandler
    fun onJoinInit(event: PlayerJoinEvent): Unit = event.player.refreshAccessories()

    // Handle accessory slot clicks
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onAccessorySlotClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        if (event.clickedInventory !== player.inventory) return
        if (event.slot !in accessorySlots) return
        // Slot is an accessory slot
        when (event.action) {
            PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
            DROP_ALL_SLOT, DROP_ONE_SLOT, MOVE_TO_OTHER_INVENTORY,
            HOTBAR_MOVE_AND_READD, HOTBAR_SWAP -> {
                // Player is trying to take an item out of the slot
                event.isCancelled = true
                val currentItem = event.currentItem
                // Is this a placeholder?
                if (plugin.placeholderUtil.test(currentItem)) return
                // Is this an accessory?
                if (AccessoryService.getInstance().test(currentItem)) {
                    // Check for special action
                    if (when (event.action) {
                            PICKUP_ONE, MOVE_TO_OTHER_INVENTORY -> event.isRightClick
                            PICKUP_HALF -> true
                            else -> false
                    }) {
                        // Special action Right-click
                        SpecialAction.RIGHT_CLICK.callEvent(player, currentItem!!)
                        return
                    }
                    when (event.action) {
                        DROP_ALL_SLOT, DROP_ONE_SLOT -> {
                            // Special action Drop key
                            SpecialAction.DROP_KEY.callEvent(player, currentItem!!)
                            return
                        }
                        else -> {}
                    }
                    // Get category
                    val category = AccessoryService.getInstance().resolveNBT(currentItem).takeIf { it.isPresent }?.get()
                        ?: event.slot.convertSlot().getCategory() // Fallback to category of the slot
                    if (player.deactivateAccessory(currentItem!!, category)) {
                        // Inject a replacement placeholder
                        event.slot.convertSlot().let { plugin.placeholderUtil.generatePlaceholder(it.getCategory(), it) }
                            .let { player.inventory.setItem(event.slot, it) }
                        // Place the accessory on the cursor
                        player.setItemOnCursor(currentItem)
                    }
                }
            }
            SWAP_WITH_CURSOR -> {
                // Player is trying to replace the item in the slot
                event.isCancelled = true // for now
                // Is the cursor item an accessory?
                if (AccessoryService.getInstance().test(event.cursor)) {
                    // Is the current item a placeholder?
                    if (plugin.placeholderUtil.test(event.currentItem)) {
                        // Get category of accessory on cursor or exit
                        val cursorCategory = event.cursor.getCategory() ?: return
                        // Fire activation event
                        if (player.activateAccessoryOnSlot(event.cursor!!, cursorCategory, event.slot.convertSlot())) {
                            // "swap" the item into the slot (overwrite the placeholder, remove item from cursor)
                            event.currentItem = event.cursor
                            event.whoClicked.setItemOnCursor(null)
                        }
                    } else if (AccessoryService.getInstance().test(event.currentItem)) {
                        // Get category of accessory on cursor or throw
                        val category = event.currentItem.getCategory()
                            ?: throw IllegalStateException("Player ${player.name} has an accessory without a category in slot #${event.slot.convertSlot()}")
                        // We need to deactivate the current accessory
                        if (player.deactivateAccessory(event.currentItem!!, category)) {
                            // Get category of accessory on cursor or exit
                            val cursorCategory = event.cursor.getCategory() ?: return
                            // If that succeeded, we'll try to activate the accessory on the cursor
                            if (player.activateAccessoryOnSlot(event.cursor!!, cursorCategory, event.slot.convertSlot())) {
                                // allow swap
                                event.isCancelled = false
                            }
                        }
                    }
                }
            }
            NOTHING, DROP_ALL_CURSOR, DROP_ONE_CURSOR, CLONE_STACK -> {}
            else -> event.isCancelled = true
        }
    }

    // Prevent drag-placement when accessory slots are involved
    @EventHandler(ignoreCancelled = true)
    fun onAccessoryDrag(event: InventoryDragEvent) {
        val slots = event.rawSlots.toMutableSet()
        event.view.bottomInventory.run {
            slots.removeIf { event.view.getInventory(it) !== this }
            if (slots.any { it in accessorySlots }) {
                event.isCancelled = true
                return
            }
        }
    }

    // Prevent accessory (slot) drops on death and designate as "to keep"
    @EventHandler(ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        // get items in accessory slots
        val contents: MutableList<ItemStack> = mutableListOf()
        accessorySlots.forEach { i -> event.player.inventory.getItem(i)?.let { contents.add(it) } }
        // remove from drops list
        event.drops.removeAll(contents)
        // add to keep
        event.itemsToKeep.addAll(contents)
    }
}
