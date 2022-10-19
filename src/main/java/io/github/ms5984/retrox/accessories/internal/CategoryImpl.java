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

import io.github.ms5984.retrox.accessories.api.Category;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

record CategoryImpl(@NotNull String name, PlaceholderTemplate template) implements Category {
    public record PlaceholderTemplate(@NotNull Material material, @NotNull String displayName, int customModelData, @NotNull List<String> lore) {
        public PlaceholderTemplate() {
            this(Material.STONE, "<white><name>", 1, List.of("<!i><white>No <name> Activated"));
        }
    }
}
