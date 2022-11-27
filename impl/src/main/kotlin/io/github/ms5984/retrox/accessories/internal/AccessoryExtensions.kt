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

import io.github.ms5984.retrox.accessories.api.AccessoryHolder
import io.github.ms5984.retrox.accessories.api.AccessoryService
import io.github.ms5984.retrox.accessories.events.AccessoryPreActivateEvent
import io.github.ms5984.retrox.accessories.events.AccessoryPreDeactivateEvent
import io.github.ms5984.retrox.accessories.events.AccessorySpecialActionEvent
import io.github.ms5984.retrox.accessories.internal.AccessoryHolderImpl.FIRST_ACCESSORY_SLOT_ID
import io.github.ms5984.retrox.accessories.internal.AccessoryHolderImpl.LAST_ACCESSORY_SLOT_ID
import io.github.ms5984.retrox.accessories.model.Accessory
import io.github.ms5984.retrox.accessories.model.Category
import io.github.ms5984.retrox.accessories.model.SpecialAction
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.Range
import java.util.UUID

val accessorySlots: IntRange = FIRST_ACCESSORY_SLOT_ID..LAST_ACCESSORY_SLOT_ID.toInt()

private val plugin
    get() = AccessoriesPlugin.instance

private val tempItem by lazy { ItemStack(Material.STONE).apply { itemMeta = itemMeta.apply {
    persistentDataContainer.set(NamespacedKey(plugin, UUID.randomUUID().toString()), PersistentDataType.BYTE, (-1).toByte())
} } }

// Refreshes accessory slots for a player
fun Player.refreshAccessories() {
    val iterator = plugin.categoryDataService.data.entries.iterator()
    for (i in 0 until AccessoryHolder.SLOTS) {
        // get category of the slot
        if (!iterator.hasNext()) break
        val (category, _) = iterator.next()
        // get index of slot in player inventory
        val calculatedSlotIndex = FIRST_ACCESSORY_SLOT_ID + i
        // get item in slot
        val existingItem = inventory.getItem(calculatedSlotIndex)
        // unless the item is a placeholder, plan to relocate it
        var relocate = !plugin.placeholderUtil.test(existingItem)
        AccessoryService.getInstance().apply {
            // Is this a valid accessory?
            if (relocate && test(existingItem)) {
                // get category of this accessory
                resolveNBT(existingItem).ifPresent {
                    // call activation event
                    if (this@refreshAccessories.activateAccessory(existingItem!!, it, AccessorySlotImpl(i, category))) {
                        // don't relocate or replace if activation was successful
                        return@ifPresent
                    }
                    relocate = true
                }
            }
            if (relocate && existingItem != null) {
                // search for vacant, appropriate slot
                val emptySlot = inventory.firstEmpty() // CB uses storage contents for this, so range is [0, 40] (plus -1)
                // -1 if no empty slot; 36-39 are armor; 40 is offhand
                if (emptySlot == -1 // no empty slot
                    || emptySlot in 36 until 40 // armor slots
                    || emptySlot in accessorySlots) {
                    // try to stuff it
                    inventory.setItem(calculatedSlotIndex, tempItem) // replace slot with temporary item
                    inventory.addItem(existingItem).takeUnless { it.isEmpty() }?.let {
                        // we will have to drop it
                        val player = this@refreshAccessories
                        for (item in it.values) player.world.dropItemNaturally(player.location, item)
                    }
                } else {
                    // we can simply move the item
                    inventory.setItem(emptySlot, existingItem)
                }
            }
        }
        // set/update/replace with placeholder
        inventory.setItem(calculatedSlotIndex, plugin.placeholderUtil.getPlaceholder(category, i))
    }
}

internal fun SpecialAction.callEvent(player: Player,
                                     accessory: @Accessory ItemStack) =
    Bukkit.getPluginManager().callEvent(AccessorySpecialActionEvent(player, accessory, this))

internal fun Player.deactivateAccessory(accessory: @Accessory ItemStack,
                                        itemCategory: Category): Boolean =
    AccessoryPreDeactivateEvent(this, itemCategory, accessory)
        .also { Bukkit.getPluginManager().callEvent(it) }
        .isCancelled.not()

internal fun Player.activateAccessory(accessory: @Accessory ItemStack,
                                      itemCategory: Category,
                                      slot: AccessorySlotImpl): Boolean =
    AccessoryPreActivateEvent(this, itemCategory, accessory, slot)
        .also { Bukkit.getPluginManager().callEvent(it) }
        .isCancelled.not()

internal fun Player.activateAccessoryOnSlot(accessory: @Accessory ItemStack,
                                            itemCategory: Category,
                                            slot: @Range(from = 0L, to = AccessoryHolder.MAX_SLOT_INDEX) Int) =
    activateAccessory(accessory, itemCategory, AccessorySlotImpl(slot, slot.getCategory()))

internal fun ItemStack?.getCategory(): Category? =
    AccessoryService.getInstance().resolveNBT(this).takeIf { it.isPresent }?.get()

internal fun @Range(from = 0L, to = AccessoryHolder.MAX_SLOT_INDEX) Int.getCategory(): Category = plugin.categoryDataService.data.keys.elementAtOrNull(this)
    ?: throw IllegalArgumentException("No category configured for accessory slot $this")

internal fun @Range(from = FIRST_ACCESSORY_SLOT_ID.toLong(), to = LAST_ACCESSORY_SLOT_ID) Int.convertSlot(): @Range(from = 0L, to = AccessoryHolder.MAX_SLOT_INDEX) Int =
    this - FIRST_ACCESSORY_SLOT_ID
