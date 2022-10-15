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

import io.github.ms5984.retrox.accessories.api.AccessoryHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
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
            if (playerInventory.getItem(FIRST_ACCESSORY_SLOT_ID + i) != null) {
                // don't overwrite existing items
                continue;
            }
            playerInventory.setItem(FIRST_ACCESSORY_SLOT_ID + i, plugin.placeholderUtil.generatePlaceholder(categoryIterator.hasNext() ? categoryIterator.next() : null, i));
        }
    }

    // Handle accessory slot clicks
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != event.getWhoClicked().getInventory()) return;
        if (event.getSlot() < FIRST_ACCESSORY_SLOT_ID || event.getSlot() >= FIRST_ACCESSORY_SLOT_ID + AccessoryHolder.SLOTS) return;
        // Slot clicked is an accessory slot
        switch (event.getAction()) {
            case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
                    DROP_ALL_SLOT, DROP_ONE_SLOT, MOVE_TO_OTHER_INVENTORY,
                    HOTBAR_MOVE_AND_READD, HOTBAR_SWAP -> {
                // Player is trying to take an item out of the slot
                var take = Component.text("TAKE");
                final var currentItem = event.getCurrentItem();
                if (currentItem != null) {
                    final var data = currentItem.getItemMeta().getPersistentDataContainer();
                    if (data.has(plugin.placeholderUtil.placeholderKey(), PersistentDataType.BYTE)) {
                        take = take.hoverEvent(HoverEvent.showText(Component.text("Placeholder for slot #" + data.get(plugin.placeholderUtil.placeholderKey(), PersistentDataType.BYTE))));
                        event.setCancelled(true);
                    }
                }
                event.getWhoClicked().sendMessage(take);
            }
            case PLACE_ALL, PLACE_SOME, PLACE_ONE, SWAP_WITH_CURSOR -> {
                // Player is trying to put an item into the slot
                event.getWhoClicked().sendMessage(Component.text("PUT"));
                event.setCancelled(true); // for now
            }
            case NOTHING, DROP_ALL_CURSOR, DROP_ONE_CURSOR, CLONE_STACK -> {}
            default -> event.setCancelled(true);
        }
    }
}
