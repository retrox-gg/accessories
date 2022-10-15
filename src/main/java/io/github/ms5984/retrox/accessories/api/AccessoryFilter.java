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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Checks if an item is an accessory.
 *
 * @since 1.0.0
 * @author ms5984
 */
@FunctionalInterface
public interface AccessoryFilter extends Predicate<ItemStack> {
    /**
     * Get the current filter instance.
     *
     * @return the current filter instance
     * @throws IllegalStateException if no filter is yet available
     */
    static @NotNull AccessoryFilter getInstance() {
        final var load = Bukkit.getServicesManager().load(AccessoryFilter.class);
        if (load == null) throw new IllegalStateException("AccessoryFilter not registered");
        return load;
    }
}
