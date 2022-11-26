package io.github.ms5984.retrox.accessories.internal
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

import io.github.ms5984.retrox.accessories.api.AccessoryService
import io.github.ms5984.retrox.accessories.api.CategoryDataService
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.HandlerList
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

/**
 * Main plugin class.
 */
class AccessoriesPlugin: JavaPlugin() {
    private val accessoryService = AccessoryServiceImpl(NamespacedKey(this, "category"))
    val categoryDataService = CategoryDataServiceImpl(this)
    val placeholderUtil = PlaceholderUtil(this, NamespacedKey(this, "placeholder"))
    val miniMessage = MiniMessage.builder()
        .tags(TagResolver.standard())
        .build()

    override fun onEnable() {
        // Plugin startup logic
        instance = this
        // register accessory service
        server.servicesManager.register(AccessoryService::class.java, accessoryService, this, ServicePriority.Normal)
        // register category data service
        server.servicesManager.register(CategoryDataService::class.java, categoryDataService, this, ServicePriority.Normal)
        saveDefaultConfig()
        config
        categoryDataService.loadCategories()
        Bukkit.getPluginManager().registerEvents(BukkitEventProcessor(this), this)
        Bukkit.getPluginManager().registerEvents(AccessoriesEventProcessor, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        HandlerList.unregisterAll(this)
        // Unregister services
        server.servicesManager.unregisterAll(this)
    }

    companion object {
        internal lateinit var instance: AccessoriesPlugin
            private set
    }
}
