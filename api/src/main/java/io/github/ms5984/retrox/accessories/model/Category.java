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

import io.github.ms5984.retrox.accessories.internal.CategoryImpl;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Identifies a category.
 *
 * @since 0.3.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface Category {
    /**
     * The regex pattern for valid category identifiers.
     * <p>
     * Explanation: Identifiers must be composed of alphanumeric characters,
     * hyphens and underscores. They must not start or end with a hyphen and
     * must not be an empty string.
     */
    @RegExp String ID_FORMAT = "^\\w(?:[\\w-]*\\w)?$";

    /**
     * Get the identifier for this category.
     *
     * @return the identifier for this category
     */
    @NotNull @Id String getId();

    /**
     * Get a category object with the specified identifier.
     *
     * @param id an identifier
     * @return a category object with the specified identifier
     * @throws IllegalArgumentException if the identifier is invalid
     */
    static @NotNull Category fromId(@NotNull @Id String id) {
        if (!id.matches(ID_FORMAT)) throw new IllegalArgumentException("Invalid category identifier: " + id);
        return new CategoryImpl(id);
    }

    /**
     * Meta-annotation for category identifiers.
     *
     * @see #ID_FORMAT
     */
    @Pattern(ID_FORMAT)
    @interface Id {}
}
