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

import io.github.ms5984.commonlib.taglib.TagLib
import org.bukkit.Material

val DEFAULT_MATERIAL = Material.STONE
const val DEFAULT_DISPLAY_NAME = "<name>"
const val DEFAULT_CUSTOM_MODEL_DATA = 1
val DEFAULT_LORE = listOf("No <name> Activated")

val DEFAULT_TEMPLATE by lazy { CategoryDataImpl.PlaceholderTemplate(
    DEFAULT_MATERIAL,
    parseDisplayName(null),
    DEFAULT_CUSTOM_MODEL_DATA,
    parseLore(emptyList())
) }

fun parseMaterial(materialName: String?) = materialName?.let { Material.matchMaterial(it) } ?: DEFAULT_MATERIAL
fun parseDisplayName(displayName: String?) = (displayName ?: DEFAULT_DISPLAY_NAME).let { TagLib.ampersand(it).displayNameOverride() }
fun parseCustomModelData(customModelData: Int?) = customModelData.takeUnless { it == 0 } ?: DEFAULT_CUSTOM_MODEL_DATA
fun parseLore(lore: List<String>) = (lore.takeUnless { it.isEmpty() } ?: DEFAULT_LORE).map { TagLib.ampersand(it).loreLineOverride() }
