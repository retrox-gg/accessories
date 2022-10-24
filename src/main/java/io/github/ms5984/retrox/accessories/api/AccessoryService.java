package io.github.ms5984.retrox.accessories.api;
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

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Manages NBT testing and namespaced key for accessory items.
 *
 * @since 0.1.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface AccessoryService extends Predicate<ItemStack> {
    /**
     * Check if an item is an accessory.
     *
     * @param itemStack an item
     * @return true if the item is an accessory
     */
    @Override
    @Contract("null -> false")
    boolean test(ItemStack itemStack);

    /**
     * Get the namespaced key used to identify accessory items.
     *
     * @return the namespaced key for accessory items
     */
    @NotNull NamespacedKey key();

    /**
     * Get the current service instance.
     *
     * @return the current service instance
     * @throws IllegalStateException if no service is yet available
     */
    static @NotNull AccessoryService getInstance() {
        final var load = Bukkit.getServicesManager().load(AccessoryService.class);
        if (load == null) throw new IllegalStateException("AccessoryService not registered");
        return load;
    }
}
