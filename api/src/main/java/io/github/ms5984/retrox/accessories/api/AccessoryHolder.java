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

import io.github.ms5984.retrox.accessories.model.Accessory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Map;

/**
 * Represents an accessory holder.
 *
 * @since 0.1.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface AccessoryHolder {
    /**
     * The number of accessory slots.
     */
    int SLOTS = 4;

    /**
     * Get the accessory in a given slot.
     * <p>
     * Slots are zero-indexed, left to right.
     *
     * @param slot the slot to check
     * @return the accessory in the slot or null if none
     */
    @Accessory @Nullable ItemStack getAccessory(@Range(from = 0, to = SLOTS-1) int slot);

    /**
     * Get all active accessories.
     * <p>
     * This is a snapshot of the current state of the accessory holder.
     * Keys are slots, values are accessories.
     *
     * @return a map of active accessories
     */
    @NotNull Map<Integer, @Accessory ItemStack> getActiveAccessories();

    /**
     * Get the player associated with this accessory holder.
     *
     * @return a player
     */
    @NotNull Player asPlayer();
}
