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
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a special action is performed on an accessory.
 *
 * @since 0.2.0
 * @author ms5984
 */
public class AccessorySpecialActionEvent extends AccessoriesEvent.Cancellable {
    private static final HandlerList HANDLERS = new HandlerList(); // per Event contract
    private final @NotNull AccessoryHolder player;
    private final @Accessory ItemStack accessory;

    /**
     * Create a new event.
     *
     * @param player a player
     * @param accessory an accessory
     */
    public AccessorySpecialActionEvent(@NotNull Player player, @Accessory ItemStack accessory) {
        this.player = new AccessoryHolderImpl(player);
        this.accessory = accessory;
    }

    /**
     * Get the player performing the special action.
     *
     * @return a player represented as an accessory holder
     */
    public @NotNull AccessoryHolder getHolder() {
        return player;
    }

    /**
     * Get the involved accessory item.
     *
     * @return an accessory item
     */
    public @Accessory ItemStack getAccessory() {
        return accessory;
    }

    // elements below required for Event contract
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
