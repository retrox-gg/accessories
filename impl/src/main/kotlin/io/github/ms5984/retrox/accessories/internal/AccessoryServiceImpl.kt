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
import io.github.ms5984.retrox.accessories.model.Category
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class AccessoryServiceImpl(private val key: NamespacedKey): AccessoryService {
    // check for presence of String data persisted using provided key
    override fun test(itemStack: ItemStack?): Boolean =
        itemStack?.takeIf(ItemStack::hasItemMeta)?.itemMeta?.persistentDataContainer?.has(key, PersistentDataType.STRING) ?: false

    override fun key(): NamespacedKey = key

    override fun addNBT(item: ItemStack, category: Category): Boolean =
        item.run {
            if (hasItemMeta()) itemMeta.persistentDataContainer.run {
                if (has(key, PersistentDataType.STRING) && get(key, PersistentDataType.STRING) == category.id) return false
            }
            itemMeta = itemMeta.apply {
                persistentDataContainer.set(key, PersistentDataType.STRING, category.id)
            }
            return true
        }

    override fun resolveNBT(item: ItemStack?): Optional<out Category> =
        item?.takeIf(::test)?.run {
            itemMeta.persistentDataContainer.get(key, PersistentDataType.STRING)
                ?.takeIf(Category.ID_FORMAT.toRegex()::matches)
                ?.run(::CategoryImpl)
        }.let { Optional.ofNullable(it) }
}
