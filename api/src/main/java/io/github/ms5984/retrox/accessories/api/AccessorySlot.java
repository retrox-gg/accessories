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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents an accessory slot.
 *
 * @since 0.1.1
 * @author ms5984
 */
public interface AccessorySlot {
    /**
     * Get the index of this slot.
     * <p>
     * Slots are zero-indexed, left to right.
     *
     * @return the index of this slot
     */
    @Range(from = 0, to = AccessoryHolder.MAX_SLOT_INDEX) int index();

    /**
     * Get the category of this slot.
     * <p>
     * Only accessories of this category should be placed in this slot.
     *
     * @return the accessory category of this slot
     */
    @NotNull Category category();
}
