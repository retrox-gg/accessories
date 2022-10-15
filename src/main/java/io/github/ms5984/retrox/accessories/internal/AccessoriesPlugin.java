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
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.PropertyResourceBundle;

/**
 * Main plugin class.
 */
public final class AccessoriesPlugin extends JavaPlugin {
    static final int FIRST_ACCESSORY_SLOT_ID = 9;
    final NamespacedKey placeholderKey = new NamespacedKey(this, "placeholder");
    final LinkedHashMap<String, CategoryImpl> categories = new LinkedHashMap<>();
    final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.standard())
            .build();

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadCategories();
        saveDefaultConfig();
        getConfig();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            // Set accessory slots to placeholders
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                final var playerInventory = event.getPlayer().getInventory();
                final var categoryIterator = categories.values().iterator();
                // set the accessory slots to placeholder items
                for (int i = 0; i < AccessoryHolder.SLOTS; ++i) {
                    if (playerInventory.getItem(FIRST_ACCESSORY_SLOT_ID + i) != null) {
                        // don't overwrite existing items
                        continue;
                    }
                    playerInventory.setItem(FIRST_ACCESSORY_SLOT_ID + i, generatePlaceholder(categoryIterator.hasNext() ? categoryIterator.next() : null, i));
                }
            }

            @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getClickedInventory() != event.getWhoClicked().getInventory()) return;
                if (event.getSlot() < FIRST_ACCESSORY_SLOT_ID || event.getSlot() >= FIRST_ACCESSORY_SLOT_ID + AccessoryHolder.SLOTS) return;
                // Slot clicked is an accessory slot
                switch (event.getAction()) {
                    case PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE,
                            DROP_ALL_SLOT, DROP_ONE_SLOT, MOVE_TO_OTHER_INVENTORY,
                            HOTBAR_MOVE_AND_READD, HOTBAR_SWAP -> {
                        // Player is trying to take an item out of the slot
                        var take = Component.text("TAKE");
                        final var currentItem = event.getCurrentItem();
                        if (currentItem != null) {
                            final var data = currentItem.getItemMeta().getPersistentDataContainer();
                            if (data.has(placeholderKey, PersistentDataType.BYTE)) {
                                take = take.hoverEvent(HoverEvent.showText(Component.text("Placeholder for slot #" + data.get(placeholderKey, PersistentDataType.BYTE))));
                                event.setCancelled(true);
                            }
                        }
                        event.getWhoClicked().sendMessage(take);
                    }
                    case PLACE_ALL, PLACE_SOME, PLACE_ONE, SWAP_WITH_CURSOR -> {
                        // Player is trying to put an item into the slot
                        event.getWhoClicked().sendMessage(Component.text("PUT"));
                        event.setCancelled(true); // for now
                    }
                    case NOTHING, DROP_ALL_CURSOR, DROP_ONE_CURSOR, CLONE_STACK -> {}
                    default -> event.setCancelled(true);
                }
            }
        }, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        HandlerList.unregisterAll(this);
    }

    private ItemStack generatePlaceholder(@Nullable Category category, @Range(from = 0, to = AccessoryHolder.SLOTS-1) int slot) {
        Material material;
        try {
            material = Material.valueOf(getConfig().getString("placeholder.material"));
        } catch (IllegalArgumentException | NullPointerException e) {
            material = Material.STONE;
        }
        final var item = new ItemStack(material);
        final var itemMeta = item.getItemMeta();
        var name = getConfig().getString("placeholder.display-name");
        if (name == null) name = "{category.name}";
        itemMeta.displayName(processCategoryData(miniMessage.deserialize("<!i><white>" + name), category));
        itemMeta.setCustomModelData(getConfig().getInt("placeholder.custom-model-data", 1));
        var lore = getConfig().getStringList("placeholder.lore");
        if (!lore.isEmpty()) {
            final ArrayList<Component> list = new ArrayList<>();
            for (String s : lore) {
                list.add(processCategoryData(miniMessage.deserialize("<!i><white>" + s), category));
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

    private void loadCategories() {
        if (!categories.isEmpty()) categories.clear();
        final var bundle = PropertyResourceBundle.getBundle("categories");
        final var keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            final var key = keys.nextElement();
            final var category = new CategoryImpl(bundle.getString(key));
            categories.put(key, category);
        }
    }
}
