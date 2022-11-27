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

import io.github.ms5984.retrox.accessories.api.AccessoryHolder
import io.github.ms5984.retrox.accessories.model.Category
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.Range
import java.util.function.Predicate

class PlaceholderUtil(private val plugin: AccessoriesPlugin,
                      private val placeholderKey: NamespacedKey): Predicate<ItemStack?> {
    internal val cachedResolutions = mutableMapOf<AccessorySlotImpl, ItemStack>()

    fun getPlaceholder(category: Category, slot: @Range(from = 0L, to = AccessoryHolder.MAX_SLOT_INDEX) Int): ItemStack =
        cachedResolutions.getOrPut(AccessorySlotImpl(slot, category)) { generatePlaceholder(category, slot) }

    private fun generatePlaceholder(category: Category, slot: Int): ItemStack =
        plugin.categoryDataService.resolve(category).let { data ->
            (data?.placeholderTemplate ?: DEFAULT_TEMPLATE).let { template ->
                ItemStack(template.material).apply {
                    itemMeta = itemMeta.apply {
                        val namePlaceholder = Placeholder.parsed("name", data?.displayName ?: category.id)
                        displayName(plugin.miniMessage.deserialize(template.displayName, namePlaceholder))
                        setCustomModelData(template.customModelData)
                        lore(template.lore.map { plugin.miniMessage.deserialize(it, namePlaceholder) })
                        persistentDataContainer.set(placeholderKey, PersistentDataType.BYTE, slot.toByte())
                    }
                }
            }
        }

    override fun test(t: ItemStack?): Boolean =
        t?.takeIf { it.hasItemMeta() }?.itemMeta?.persistentDataContainer?.has(placeholderKey, PersistentDataType.BYTE) ?: false
}
