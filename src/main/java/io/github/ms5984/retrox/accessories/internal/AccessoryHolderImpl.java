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
import io.github.ms5984.retrox.accessories.model.Accessory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

public class AccessoryHolderImpl implements AccessoryHolder {
    private final Player player;
    private final ItemStack[] accessorySlots;

    public AccessoryHolderImpl(@NotNull Player player) {
        this.player = player;
        this.accessorySlots = new ItemStack[SLOTS];
        System.arraycopy(player.getInventory().getContents(), 9, accessorySlots, 0, SLOTS);
    }

    @Override
    public @Accessory @Nullable ItemStack getAccessory(@Range(from = 0, to = SLOTS-1) int slot) {
        return accessorySlots[slot];
    }

    @Override
    public @NotNull Map<Integer, @Accessory ItemStack> getActiveAccessories() {
        final HashMap<Integer, @Accessory ItemStack> map = new HashMap<>();
        for (int i = 0; i < SLOTS; i++) {
            final ItemStack itemStack = accessorySlots[i];
            if (itemStack != null) map.put(i, itemStack);
        }
        return map;
    }

    @Override
    public @NotNull Player asPlayer() {
        return player;
    }
}
