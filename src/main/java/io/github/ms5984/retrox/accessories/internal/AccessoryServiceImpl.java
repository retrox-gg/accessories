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
import io.github.ms5984.retrox.accessories.api.Category;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

record AccessoryServiceImpl(@NotNull AccessoriesPlugin plugin, @NotNull NamespacedKey key) implements AccessoryService {
    @Override
    public boolean test(ItemStack itemStack) {
        // simply check for presence of any data persisted using provided key
        return itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(key);
    }

    @Override
    public boolean addNBT(@NotNull ItemStack item, @NotNull Category category) {
        final var id = plugin.categoriesService.getId(category);
        if (id.isPresent()) {
            final var meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id.get());
            item.setItemMeta(meta);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Optional<CategoryImpl> resolveNBT(@NotNull ItemStack item) {
        final var data = item.getItemMeta().getPersistentDataContainer();
        if (data.has(key, PersistentDataType.STRING)) {
            return plugin.categoriesService.getCategory(data.get(key, PersistentDataType.STRING));
        }
        return Optional.empty();
    }
}
