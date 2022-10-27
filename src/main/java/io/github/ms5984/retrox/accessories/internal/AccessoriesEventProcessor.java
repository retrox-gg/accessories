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
import io.github.ms5984.retrox.accessories.events.AccessoryPreActivateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.1.1
 * @param plugin plugin instance
 */
record AccessoriesEventProcessor(@NotNull AccessoriesPlugin plugin) implements Listener {
    // prevent placement of accessories in slots according to category
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAccessoryActivateCategoryDetect(AccessoryPreActivateEvent event) {
        // If the accessory is not in the category, cancel the event
        final var category = event.getTargetSlot().category();
        if (AccessoryService.getInstance()
                .resolveNBT(event.getActivatingAccessory())
                .filter(category::equals)
                .isEmpty()) {
            // The category is not the same
            event.setCancelled(true);
        }
    }
}
