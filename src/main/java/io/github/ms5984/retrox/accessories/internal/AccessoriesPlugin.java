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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class.
 */
public final class AccessoriesPlugin extends JavaPlugin {
    private final AccessoryServiceImpl accessoryFilter = new AccessoryServiceImpl(new NamespacedKey(this, "accessory"));
    final CategoriesService categoriesService = new CategoriesService(this);
    final PlaceholderUtil placeholderUtil = new PlaceholderUtil(this, new NamespacedKey(this, "placeholder"));
    final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.standard())
            .build();

    @Override
    public void onEnable() {
        // Plugin startup logic
        categoriesService.loadCategories();
        // register accessory filter
        Bukkit.getServicesManager().register(AccessoryService.class, accessoryFilter, this, ServicePriority.Normal);
        saveDefaultConfig();
        getConfig();
        Bukkit.getPluginManager().registerEvents(new BukkitEventProcessor(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        HandlerList.unregisterAll(this);
        Bukkit.getServicesManager().unregisterAll(this);
    }
}
