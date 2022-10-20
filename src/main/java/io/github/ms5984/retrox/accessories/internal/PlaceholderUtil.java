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
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.function.Predicate;

record PlaceholderUtil(@NotNull AccessoriesPlugin plugin, @NotNull NamespacedKey placeholderKey) implements Predicate<ItemStack> {
    public ItemStack generatePlaceholder(@Nullable CategoryImpl category, @Range(from = 0, to = AccessoryHolder.SLOTS-1) int slot) {
        final var template = category == null ? new CategoryImpl.PlaceholderTemplate() : category.template();
        final var item = new ItemStack(template.material());
        final var itemMeta = item.getItemMeta();
        itemMeta.displayName(resolveCategoryComponent(template.displayName(), category));
        itemMeta.setCustomModelData(template.customModelData());
        final ArrayList<Component> lore = new ArrayList<>();
        for (final var line : template.lore()) {
            lore.add(resolveCategoryComponent(line, category));
        }
        itemMeta.lore(lore);
        itemMeta.getPersistentDataContainer().set(placeholderKey, PersistentDataType.BYTE, (byte) slot);
        item.setItemMeta(itemMeta);
        return item;
    }

    private Component resolveCategoryComponent(@NotNull String mmText, @Nullable CategoryImpl category) {
        if (category == null) return plugin.miniMessage.deserialize(mmText);
        return plugin.miniMessage.deserialize(mmText, Placeholder.parsed("name", category.name()));
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(placeholderKey, PersistentDataType.BYTE);
    }
}
