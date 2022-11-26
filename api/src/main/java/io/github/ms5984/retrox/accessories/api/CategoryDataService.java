package io.github.ms5984.retrox.accessories.api;
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

import io.github.ms5984.retrox.accessories.model.Category;
import io.github.ms5984.retrox.accessories.model.CategoryData;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides metadata about categories.
 *
 * @since 0.3.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface CategoryDataService {
    /**
     * Look for a category's metadata.
     *
     * @param category the category to resolve
     * @return the category's metadata or null if none known
     */
    @Nullable CategoryData resolve(@NotNull Category category);

    /**
     * Get the current service instance.
     *
     * @return the current service instance
     * @throws IllegalStateException if no service is yet available
     */
    static @NotNull CategoryDataService getInstance() {
        final var load = Bukkit.getServicesManager().load(CategoryDataService.class);
        if (load == null) throw new IllegalStateException("CategoryDataService not registered");
        return load;
    }
}
