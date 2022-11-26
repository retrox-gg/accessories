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

import io.github.ms5984.retrox.accessories.api.CategoryDataService
import io.github.ms5984.retrox.accessories.model.Category

class CategoryDataServiceImpl(private val plugin: AccessoriesPlugin): CategoryDataService {
    val data: MutableMap<Category, CategoryDataImpl> = mutableMapOf()

    override fun resolve(category: Category): CategoryDataImpl? = data[category]

    fun loadCategories() {
        if (data.isNotEmpty()) data.clear()
        plugin.config.getConfigurationSection("categories")?.run {
            val idRegex = Category.ID_FORMAT.toRegex()
            var skips = false
            // this = top-level categories section (section of sections)
            for (id in getKeys(false)) {
                val category = id.takeIf { it.matches(idRegex) }?.let(::CategoryImpl)
                if (category == null) {
                    plugin.logger.warning("Invalid category ID: $id. Skipping.")
                    skips = true
                    continue
                }
                getConfigurationSection(id)?.run {
                    // this = a category section
                    val name = getString("name")
                    val template = getConfigurationSection("placeholder")?.run {
                        // this = placeholder section
                        // See config.yml for more information on defaults
                        CategoryDataImpl.PlaceholderTemplate(
                            parseMaterial(getString("material")),
                            parseDisplayName(getString("display-name")),
                            parseCustomModelData(getInt("custom-model-data")),
                            parseLore(getStringList("lore"))
                        )
                    } ?: CategoryDataImpl.PlaceholderTemplate()
                    if (name == null) data[category] = CategoryDataImpl({ category }, template)
                    else data[category] = CategoryDataImpl({ category }, template, mutableMapOf("display-name" to name))
                }
            }
            if (skips) plugin.logger.apply {
                info("Some categories were skipped. Please check your config.yml for errors.")
                info("Category IDs must match the regex: ${Category.ID_FORMAT}")
            }
        }
    }
}
