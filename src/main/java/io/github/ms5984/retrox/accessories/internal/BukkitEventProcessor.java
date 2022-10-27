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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;

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
                // get category of the slot (this might also fix a bug where category is skipped)
                if (!categoryIterator.hasNext()) break;
                final var category = categoryIterator.next();
                // call activation event
                if (activateAccessory(event.getPlayer(), existingItem, new AccessorySlotImpl(i, category))) {
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
                        if (deactivateAccessory(player, currentItem)) {
                            // Inject a replacement placeholder
                            final int accessorySlot = convertSlot(event.getSlot());
                            final var iterator = plugin.categoriesService.iterator();
                            int i = 0;
                            while (i < accessorySlot && iterator.hasNext()) {
                                iterator.next();
                                ++i;
                            }
                            if (!iterator.hasNext()) throw new IllegalStateException("No category configured for accessory slot " + accessorySlot);
                            player.getInventory().setItem(event.getSlot(), plugin.placeholderUtil.generatePlaceholder(iterator.next(), i));
                            // Place the accessory on the cursor
                            player.setItemOnCursor(currentItem);
                        }
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
                            if (activateAccessoryCalculateCategory(player, event.getCursor(), convertSlot(event.getSlot()))) {
                                // "swap" the item (replace the placeholder, remove the cursor item)
                                event.setCurrentItem(event.getCursor());
                                event.getWhoClicked().setItemOnCursor(null);
                            }
                        } else if (AccessoryService.getInstance().test(currentItem)) {
                            // We need to deactivate the current accessory
                            if (deactivateAccessory(player, currentItem)) {
                                // If that succeeded, fire activation event
                                if (activateAccessoryCalculateCategory(player, event.getCursor(), convertSlot(event.getSlot()))) {
                                    // allow swap
                                    event.setCancelled(false);
                                }
                            }
                        }
                    }
                }
                case NOTHING, DROP_ALL_CURSOR, DROP_ONE_CURSOR, CLONE_STACK -> {}
                default -> event.setCancelled(true);
            }
        }
    }

    // Prevent drag-placement of any accessory
    @EventHandler(ignoreCancelled = true)
    public void onAccessoryDrag(InventoryDragEvent event) {
        if (AccessoryService.getInstance().test(event.getOldCursor())) event.setCancelled(true);
    }

    // Prevent accessory (slot) drops on death and designate as "to keep"
    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        // get items in accessory slots
        final ArrayList<ItemStack> accessorySlots = new ArrayList<>(AccessoryHolder.SLOTS);
        for (int i = 0; i < AccessoryHolder.SLOTS; ++i) {
            accessorySlots.add(event.getPlayer().getInventory().getItem(FIRST_ACCESSORY_SLOT_ID + i));
        }
        // remove from drops
        event.getDrops().removeAll(accessorySlots);
        // add to keep
        event.getItemsToKeep().addAll(accessorySlots);
    }

    private boolean deactivateAccessory(Player player, ItemStack currentItem) {
        // Fire event
        final var preDeactivateEvent = new AccessoryPreDeactivateEvent(player, currentItem);
        Bukkit.getPluginManager().callEvent(preDeactivateEvent);
        return !preDeactivateEvent.isCancelled();
    }

    private boolean activateAccessory(Player player, ItemStack accessory, AccessorySlotImpl slot) {
        final var preActivateEvent = new AccessoryPreActivateEvent(player, accessory, slot);
        Bukkit.getPluginManager().callEvent(preActivateEvent);
        return !preActivateEvent.isCancelled();
    }

    private boolean activateAccessoryCalculateCategory(Player player, ItemStack accessory, int index) {
        // Get the slot category
        final var iterator = plugin.categoriesService.iterator();
        int i = 0;
        while (i < index && iterator.hasNext()) {
            iterator.next();
            ++i;
        }
        if (!iterator.hasNext()) throw new IllegalStateException("No category configured for accessory slot " + index);
        return activateAccessory(player, accessory, new AccessorySlotImpl(index, iterator.next()));
    }

    //
    private static @Range(from = 0, to = AccessoryHolder.SLOTS - 1) int convertSlot(@Range(from = FIRST_ACCESSORY_SLOT_ID, to = FIRST_ACCESSORY_SLOT_ID + AccessoryHolder.SLOTS) int slot) {
        return slot - FIRST_ACCESSORY_SLOT_ID;
    }
}
