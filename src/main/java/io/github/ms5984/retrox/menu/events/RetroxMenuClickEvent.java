package io.github.ms5984.retrox.menu.events;
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

import io.github.ms5984.retrox.menu.model.ActionKey;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a click event in a RetroxMenu.
 *
 * @since 1.0.0
 * @author ms5984
 */
public final class RetroxMenuClickEvent extends RetroxMenuEvent.Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final @ActionKey String action;
    private final ClickType clickType;

    public RetroxMenuClickEvent(@ActionKey String action, ClickType clickType) {
        this.action = action;
        this.clickType = clickType;
    }

    /**
     * Get the action key associated with this event.
     *
     * @return an action key
     */
    public @ActionKey String action() {
        return action;
    }

    /**
     * Get the click type associated with this event.
     *
     * @return the click type
     */
    public ClickType clickType() {
        return clickType;
    }

    // elements below required for Event contract
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
