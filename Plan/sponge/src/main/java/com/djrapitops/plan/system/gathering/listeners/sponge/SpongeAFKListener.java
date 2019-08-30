/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.system.gathering.listeners.sponge;

import com.djrapitops.plan.system.gathering.afk.AFKTracker;
import com.djrapitops.plan.system.settings.Permissions;
import com.djrapitops.plan.system.settings.config.PlanConfig;
import com.djrapitops.plugin.logging.L;
import com.djrapitops.plugin.logging.error.ErrorHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener that keeps track of actions that are not considered being AFK.
 * <p>
 * Additional Listener calls in PlayerOnlineListener to avoid having HIGHEST priority listeners.
 *
 * @author Rsl1122
 * @see PlayerOnlineListener
 */
public class SpongeAFKListener {

    // Static so that /reload does not cause afk tracking to fail.
    static AFKTracker AFK_TRACKER;

    private final Map<UUID, Boolean> ignorePermissionInfo;
    private final ErrorHandler errorHandler;

    @Inject
    public SpongeAFKListener(PlanConfig config, ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.ignorePermissionInfo = new HashMap<>();

        SpongeAFKListener.assignAFKTracker(config);
    }

    private static void assignAFKTracker(PlanConfig config) {
        if (AFK_TRACKER == null) {
            AFK_TRACKER = new AFKTracker(config);
        }
    }

    private void event(TargetPlayerEvent event) {
        try {
            performedAction(event.getTargetEntity());
        } catch (Exception e) {
            errorHandler.log(L.ERROR, this.getClass(), e);
        }
    }

    @Listener(order = Order.POST)
    public void onMove(MoveEntityEvent event, @First Player player) {
        performedAction(player);
    }

    @Listener(order = Order.POST)
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
        performedAction(player);
    }

    private void performedAction(Player player) {
        UUID uuid = player.getUniqueId();
        long time = System.currentTimeMillis();

        Boolean ignored = ignorePermissionInfo.get(uuid);
        if (ignored == null) {
            ignored = player.hasPermission(Permissions.IGNORE_AFK.getPermission());
        }
        if (ignored) {
            AFK_TRACKER.hasIgnorePermission(uuid);
            ignorePermissionInfo.put(uuid, true);
            return;
        } else {
            ignorePermissionInfo.put(uuid, false);
        }

        AFK_TRACKER.performedAction(uuid, time);
    }

    @Listener(order = Order.POST)
    public void onPlayerCommand(SendCommandEvent event, @First Player player) {
        performedAction(player);

        boolean isAfkCommand = event.getCommand().toLowerCase().startsWith("afk");
        if (isAfkCommand) {
            AFK_TRACKER.usedAfkCommand(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Listener(order = Order.POST)
    public void onSettingsChange(PlayerChangeClientSettingsEvent event) {
        event(event);
    }

    @Listener(order = Order.POST)
    public void onLeave(ClientConnectionEvent.Disconnect event) {
        ignorePermissionInfo.remove(event.getTargetEntity().getUniqueId());
    }
}