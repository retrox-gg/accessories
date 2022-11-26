package io.github.ms5984.retrox.accessories.model;
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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Access metadata about a category.
 *
 * @since 0.3.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface CategoryData {
    /**
     * Get the category associated with this metadata.
     *
     * @return the category associated with this metadata
     */
    @NotNull Category getCategory();

    /**
     * Get the formal display name for this category.
     *
     * @return a display name or the category's identifier if none set
     * @implSpec If no display name is set this method should return the
     * associated category's identifier by {@code getCategory().getId()}.
     */
    @NotNull String getDisplayName();

    /**
     * Get the raw properties map associated with this category.
     *
     * @return the raw properties map associated with this category
     * @implSpec The returned map is immutable.
     */
    @NotNull Map<String, Object> getProperties();
}
