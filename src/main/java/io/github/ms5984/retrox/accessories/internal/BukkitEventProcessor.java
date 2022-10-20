package io.github.ms5984.retrox.accessories.internal;
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

import io.github.ms5984.retrox.accessories.api.AccessoryService;
import io.github.ms5984.retrox.accessories.api.AccessoryHolder;
import io.github.ms5984.retrox.accessories.events.AccessoryPreActivateEvent;
import io.github.ms5984.retrox.accessories.events.AccessoryPreDeactivateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.ms5984.retrox.accessories.internal.AccessoryHolderImpl.FIRST_ACCESSORY_SLOT_ID;

record BukkitEventProcessor(@NotNull AccessoriesPlugin plugin) implements Listener {
    // Set accessory slots to placeholders
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final var playerInventory = event.getPlayer().getInventory();
        final var categoryIterator = plugin.categoriesService.iterator();
        // set the accessory slots to placeholder items
        for (int i = 0; i < AccessoryHolder.SLOTS; ++i) {
            final var calculatedSlotIndex = FIRST_ACCESSORY_SLOT_ID + i;
            final var existingItem = playerInventory.getItem(calculatedSlotIndex);
            // unless the slot is a placeholder, plan to relocate it
            boolean relocate = !plugin.placeholderUtil.test(existingItem);
            // Check if what we have is a valid accessory
            if (relocate && AccessoryService.getInstance().test(existingItem)) {
                // call activation event
                if (activateAccessory(event.getPlayer(), existingItem)) {
                    // don't relocate or replace if activation was successful
                    continue;
                }
            }
            if (relocate && existingItem != null) {
                // search for vacant and appropriate slot
                final int emptySlot = playerInventory.firstEmpty(); // CB uses storage contents for this, so range is [0, 40] (plus -1)
                // -1 if no empty slot; 36-39 are armor; 40 is offhand
                if (emptySlot == -1 || (emptySlot > 35 && emptySlot != 40) || (emptySlot >= FIRST_ACCESSORY_SLOT_ID && emptySlot <= FIRST_ACCESSORY_SLOT_ID + AccessoryHolder.SLOTS)) {
                    // try to stuff it
                    playerInventory.setItem(calculatedSlotIndex, null);
                    final var stuff = playerInventory.addItem(existingItem);
                    if (!stuff.isEmpty()) {
                        // we will have to drop it
                        for (ItemStack item : stuff.values()) {
                            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), item);
                        }
                    }
                } else {
                    // we can simply move the item
                    playerInventory.setItem(emptySlot, existingItem);
                }
            }
            // set/replace with placeholder
            playerInventory.setItem(calculatedSlotIndex, plugin.placeholderUtil.generatePlaceholder(categoryIterator.hasNext() ? categoryIterator.next() : null, i));
        }
    }

    // Handle accessory slot clicks
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (event.getClickedInventory() != player.getInventory()) return;
            if (event.getSlot() < FIRST_ACCESSORY_SLOT_ID || event.getSlot() >= FIRST_ACCESSORY_SLOT_ID + AccessoryHolder.SLOTS) return;
            // Slot clicked is an accessory slot
            switch (event.getAction()) {
                case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
                        DROP_ALL_SLOT, DROP_ONE_SLOT, MOVE_TO_OTHER_INVENTORY,
                        HOTBAR_MOVE_AND_READD, HOTBAR_SWAP -> {
                    // Player is trying to take an item out of the slot
                    event.setCancelled(true);
                    final var currentItem = event.getCurrentItem();
                    // Is this an accessory?
                    if (AccessoryService.getInstance().test(currentItem)) {
                        deactivateAccessory(event, player, currentItem);
                    } else if (plugin.placeholderUtil.test(currentItem)) {
                        plugin.getComponentLogger().debug("Player {} tried to take a placeholder item from accessory slot #{}", player.getName(), event.getSlot());
                    }
                }
                case SWAP_WITH_CURSOR -> {
                    // Player is trying to replace the item in the slot
                    event.setCancelled(true); // for now
                    // Is the cursor item an accessory?
                    if (AccessoryService.getInstance().test(event.getCursor())) {
                        final var currentItem = event.getCurrentItem();
                        // Is this a placeholder?
                        if (plugin.placeholderUtil.test(currentItem)) {
                            // Fire event
                            if (activateAccessory(player, event.getCursor())) {
                                // "swap" the item (replace the placeholder, remove the cursor item)
                                event.setCurrentItem(event.getCursor());
                                event.getWhoClicked().setItemOnCursor(null);
                            }
                        } else if (AccessoryService.getInstance().test(currentItem)) {
                            // We need to deactivate the current accessory
                            if (deactivateAccessory(event, player, currentItem)) {
                                // If that succeeded, fire activation event
                                if (activateAccessory(player, event.getCursor())) {
                                    // allow swap
                                    event.setCancelled(false);
                                }
                            }
                        }
                    }
                }
                case NOTHING, PLACE_ALL, PLACE_SOME, PLACE_ONE, DROP_ALL_CURSOR, DROP_ONE_CURSOR, CLONE_STACK -> {}
                default -> event.setCancelled(true);
            }
        }
    }

    private boolean deactivateAccessory(InventoryClickEvent event, Player player, ItemStack currentItem) {
        // Fire event
        final var preDeactivateEvent = new AccessoryPreDeactivateEvent(player, currentItem);
        Bukkit.getPluginManager().callEvent(preDeactivateEvent);
        final var cancelled = preDeactivateEvent.isCancelled();
        if (!cancelled) {
            event.setCancelled(false);
            // Inject a replacement placeholder
            final var iterator = plugin.categoriesService.iterator();
            if (iterator.hasNext()) {
                int i = 0;
                while (i < event.getSlot() - FIRST_ACCESSORY_SLOT_ID) {
                    if (i > AccessoryHolder.SLOTS) break;
                    iterator.next();
                    ++i;
                }
                player.getInventory().setItem(event.getSlot(), plugin.placeholderUtil.generatePlaceholder(iterator.next(), i));
            }
        }
        return !cancelled;
    }

    private boolean activateAccessory(Player player, ItemStack accessory) {
        final var preActivateEvent = new AccessoryPreActivateEvent(player, accessory);
        Bukkit.getPluginManager().callEvent(preActivateEvent);
        return !preActivateEvent.isCancelled();
    }
}
