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

import io.github.ms5984.retrox.accessories.api.AccessoryHolder;
import io.github.ms5984.retrox.accessories.api.AccessorySlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * @param index the index of this slot
 * @param category the accessory category of this slot
 * @since 0.1.1
 */
record AccessorySlotImpl(@Range(from = 0, to = AccessoryHolder.SLOTS - 1) int index, @NotNull CategoryImpl category) implements AccessorySlot {}
