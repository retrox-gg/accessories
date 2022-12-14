package io.github.ms5984.retrox.accessories.events;
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
import io.github.ms5984.retrox.accessories.internal.AccessoryHolderImpl;
import io.github.ms5984.retrox.accessories.model.Accessory;
import io.github.ms5984.retrox.accessories.model.Category;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called as an accessory is being deactivated.
 *
 * @since 0.1.0
 * @author ms5984
 */
public final class AccessoryPreDeactivateEvent extends AccessoriesEvent.Cancellable {
    private static final HandlerList HANDLERS = new HandlerList(); // per Event contract
    private final @NotNull AccessoryHolder player;
    private final @NotNull Category category;
    private final @Accessory ItemStack deactivatingAccessory;

    /**
     * Create a new event.
     *
     * @param player the player deactivating the accessory
     * @param category the category of the deactivating accessory
     * @param deactivatingAccessory the accessory being deactivated
     */
    public AccessoryPreDeactivateEvent(@NotNull Player player,
                                       @NotNull Category category,
                                       @Accessory ItemStack deactivatingAccessory) {
        this.player = new AccessoryHolderImpl(player);
        this.category = category;
        this.deactivatingAccessory = deactivatingAccessory;
    }

    /**
     * Get the player deactivating the accessory.
     *
     * @return a player represented as an accessory holder
     */
    public @NotNull AccessoryHolder getHolder() {
        return player;
    }

    /**
     * Get the accessory being deactivated.
     *
     * @return the accessory being deactivated
     */
    public @Accessory ItemStack getDeactivatingAccessory() {
        return deactivatingAccessory;
    }

    /**
     * Get the category to which the deactivating accessory belongs.
     *
     * @return the accessory category
     * @since 0.1.1
     */
    public @NotNull Category getCategory() {
        return category;
    }

    // elements below required for Event contract
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
