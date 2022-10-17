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
import io.github.ms5984.retrox.accessories.api.Category;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.function.Predicate;

record PlaceholderUtil(@NotNull AccessoriesPlugin plugin, @NotNull NamespacedKey placeholderKey) implements Predicate<ItemStack> {
    public ItemStack generatePlaceholder(@Nullable Category category, @Range(from = 0, to = AccessoryHolder.SLOTS-1) int slot) {
        Material material;
        try {
            material = Material.valueOf(plugin.getConfig().getString("placeholder.material"));
        } catch (IllegalArgumentException | NullPointerException e) {
            material = Material.STONE;
        }
        final var item = new ItemStack(material);
        final var itemMeta = item.getItemMeta();
        var name = plugin.getConfig().getString("placeholder.display-name");
        if (name == null) name = "{category.name}";
        itemMeta.displayName(processCategoryData(plugin.miniMessage.deserialize("<!i><white>" + name), category));
        itemMeta.setCustomModelData(plugin.getConfig().getInt("placeholder.custom-model-data", 1));
        var lore = plugin.getConfig().getStringList("placeholder.lore");
        if (!lore.isEmpty()) {
            final ArrayList<Component> list = new ArrayList<>();
            for (String s : lore) {
                list.add(processCategoryData(plugin.miniMessage.deserialize("<!i><white>" + s), category));
            }
            itemMeta.lore(list);
        }
        itemMeta.getPersistentDataContainer().set(placeholderKey, PersistentDataType.BYTE, (byte) slot);
        item.setItemMeta(itemMeta);
        return item;
    }

    private Component processCategoryData(Component component, @Nullable Category category) {
        if (category == null) return component;
        return component.replaceText(b -> b.matchLiteral("{category.name}").replacement(category.name()));
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(placeholderKey, PersistentDataType.BYTE);
    }
}
