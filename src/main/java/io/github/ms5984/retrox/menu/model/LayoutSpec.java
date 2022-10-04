package io.github.ms5984.retrox.menu.model;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a menu layout.
 *
 * @since 1.0.0
 * @author ms5984
 */
public class LayoutSpec {
    /**
     * Represents a slot in the layout.
     *
     * @since 1.0.0
     */
    public enum SlotType {
        ACTION_ELEMENT('a'),
        MENU_LEFT('l'),
        MENU_RIGHT('r'),
        ;

        private final char match;

        SlotType(char match) {
            this.match = match;
        }

        /**
         * Get the character that matches this slot type.
         *
         * @return the character
         */
        public char getMatch() {
            return match;
        }
    }
    private final Map<Integer, SlotType> slots;
    private final int inventorySize;
    private final int maxActionElementCount;

    /**
     * Create a new LayoutSpec.
     * <p>
     * The map contents will be copied.
     *
     * @param slots a map of slot numbers to slot types
     * @throws IllegalArgumentException if the map contains invalid slots
     */
    public LayoutSpec(@NotNull Map<@Range(from = 0, to = 53) Integer, SlotType> slots) throws IllegalArgumentException {
        slots.keySet().forEach(LayoutSpec::validateSlot);
        this.slots = Map.copyOf(slots);
        this.maxActionElementCount = (int) this.slots.values().stream().filter(SlotType.ACTION_ELEMENT::equals).count();
        this.inventorySize = slots.keySet().stream()
                .max(Integer::compareTo)
                .map(slot -> (slot % 9 == 0) ? slot : (slot / 9 + 1) * 9)
                .orElse(9);
    }

    /**
     * Create a new LayoutSpec.
     * <p>
     * The map contents will be copied.
     *
     * @param slots a map of slot numbers to slot types
     * @param inventorySize the size of the final inventory
     * @throws IllegalArgumentException if the map contains invalid slots
     * or contains a key greater than <code>inventorySize</code>
     */
    public LayoutSpec(@NotNull Map<@Range(from = 0, to = 53) Integer, SlotType> slots, @Range(from = 9, to = 54) int inventorySize) throws IllegalArgumentException {
        slots.keySet().stream().mapToInt(i -> i).max().ifPresent(max -> {
            validateSlot(max);
            if (max > inventorySize) {
                throw new IllegalArgumentException("Inventory size must be greater than or equal to the largest provided slot number");
            }
        });
        this.slots = Map.copyOf(slots);
        this.inventorySize = inventorySize;
        this.maxActionElementCount = (int) this.slots.values().stream().filter(SlotType.ACTION_ELEMENT::equals).count();
    }

    /**
     * Get the slot mappings reflecting this layout.
     *
     * @return a map of slot numbers to slot types
     */
    public Map<@Range(from = 0, to = 53) Integer, SlotType> getSlots() {
        return slots;
    }

    /**
     * Get the maximum possible number of action elements in the layout.
     *
     * @return the maximum number of action elements
     */
    public @Range(from = 0, to = 54) int getMaxActionElementCount() {
        return maxActionElementCount;
    }

    /**
     * Get the size of the final inventory.
     *
     * @return the inventory size
     */
    public @Range(from = 9, to = 54) int getInventorySize() {
        return inventorySize;
    }

    static void validateSlot(int slot) throws IllegalArgumentException {
        if (slot < 0 || slot > 53) {
            throw new IllegalArgumentException("Slot indexes must be between 0 and 53");
        }
    }

    /**
     * Create an empty LayoutSpec builder with an initial
     * <code>inventorySize</code> of 9.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for LayoutSpec.
     *
     * @since 1.0.0
     */
    public static class Builder {
        private final Map<Integer, SlotType> slots = new HashMap<>();
        private int inventorySize = 9;

        Builder() {}

        /**
         * Create an empty LayoutSpec builder with the specified
         * <code>inventorySize</code>.
         *
         * @param inventorySize the initial inventory size
         */
        public Builder(int inventorySize) {
            this.inventorySize = inventorySize;
        }

        /**
         * Create a LayoutSpec builder from an existing LayoutSpec.
         *
         * @param spec the LayoutSpec to copy
         */
        public Builder(@NotNull LayoutSpec spec) {
            this.slots.putAll(spec.slots);
            this.inventorySize = spec.inventorySize;
        }

        /**
         * Get the current inventory size.
         *
         * @return the current inventory size
         */
        public int inventorySize() {
            return inventorySize;
        }

        /**
         * Set the inventory size.
         *
         * @param inventorySize the inventory size
         * @return this builder
         * @throws IllegalArgumentException if <code>inventorySize</code>
         * is not divisible by 9
         */
        public Builder inventorySize(@Range(from = 9, to = 54) int inventorySize) throws IllegalArgumentException {
            this.inventorySize = inventorySize;
            return this;
        }

        /**
         * Set a slot's type.
         * <p>
         * Use null to remove a mapping.
         *
         * @param slot the slot number
         * @param type the new slot type or null
         * @return this builder
         * @throws IllegalArgumentException if <code>slot</code> out of range
         */
        public Builder slot(@Range(from = 0, to = 53) int slot, @Nullable SlotType type) throws IllegalArgumentException {
            if (type == null) {
                slots.remove(slot);
            } else {
                validateSlot(slot);
                slots.put(slot, type);
            }
            return this;
        }

        /**
         * Build a LayoutSpec.
         *
         * @return the LayoutSpec
         */
        public LayoutSpec build() {
            return new LayoutSpec(slots, inventorySize);
        }
    }
}
