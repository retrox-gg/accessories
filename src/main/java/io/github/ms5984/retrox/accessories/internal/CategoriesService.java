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

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

final class CategoriesService {
    private final LinkedHashMap<String, CategoryImpl> categories = new LinkedHashMap<>();
    private final AccessoriesPlugin plugin;

    CategoriesService(AccessoriesPlugin plugin) {
        this.plugin = plugin;
    }

    void loadCategories() {
        if (!categories.isEmpty()) categories.clear();
        final var topSection = plugin.getConfig().getConfigurationSection("categories");
        if (topSection == null) return;
        for (String id : topSection.getKeys(false)) {
            final var subSection = topSection.getConfigurationSection(id);
            if (subSection == null) continue;
            final var name = subSection.getString("name", id);
            final var placeholderSection = subSection.getConfigurationSection("placeholder");
            final CategoryImpl.PlaceholderTemplate template;
            if (placeholderSection == null) {
                template = new CategoryImpl.PlaceholderTemplate();
            } else {
                // See config.yml for more information on defaults
                final var material = parseMaterial(placeholderSection.getString("material"));
                final var displayName = parseDisplayName(placeholderSection.getString("display-name"));
                final var customModelData = placeholderSection.getInt("custom-model-data", 1);
                final var lore = parseLore(placeholderSection.getStringList("lore"));
                template = new CategoryImpl.PlaceholderTemplate(material, displayName, customModelData, lore);
            }
            categories.put(id, new CategoryImpl(id, template));
        }
    }

    public Iterator<CategoryImpl> iterator() {
        return categories.values().iterator();
    }

    // defaults to STONE
    static Material parseMaterial(String materialName) {
        if (materialName == null) return Material.STONE;
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            return Material.STONE;
        }
    }

    static String parseDisplayName(String displayName) {
        if (displayName == null) return "<white><name>";
        return displayName;
    }

    static List<String> parseLore(@NotNull List<String> lore) {
        if (lore.isEmpty()) return List.of("<!i><white>No <name> Activated");
        return List.copyOf(lore);
    }
}
