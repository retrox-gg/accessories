package io.github.ms5984.retrox.accessories.events;
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

import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for Accessories events.
 *
 * @since 1.0.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public abstract class AccessoriesEvent extends Event {
    /**
     * Base class for Accessories events that can be cancelled.
     *
     * @since 1.0.0
     */
    public abstract static class Cancellable extends AccessoriesEvent implements org.bukkit.event.Cancellable {
        private boolean cancelled;

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            cancelled = cancel;
        }
    }
}
