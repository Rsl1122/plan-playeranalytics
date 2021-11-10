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
package com.djrapitops.plan.gathering.domain;

import com.djrapitops.plan.delivery.domain.DateHolder;
import com.djrapitops.plan.delivery.domain.PlayerIdentifier;
import com.djrapitops.plan.delivery.domain.ServerIdentifier;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a player vs player kill.
 *
 * @author AuroraLS3
 */
public class PlayerKill implements DateHolder {

    private final Killer killer;
    private final Victim victim;
    private final String weapon;
    private final long date;
    private final ServerIdentifier server;

    /**
     * Creates a PlayerKill object with given parameters.
     *
     * @param killer UUID of the killer.
     * @param victim UUID of the victim.
     * @param weapon Weapon used.
     * @param date   Epoch millisecond at which the kill occurred.
     */
    @Deprecated
    public PlayerKill(UUID killer, UUID victim, String weapon, long date) {
        this.killer = new Killer(killer, null);
        this.victim = new Victim(victim, null);
        this.weapon = weapon;
        this.date = date;
        this.server = new ServerIdentifier(null, "");
    }

    @Deprecated
    public PlayerKill(UUID killer, UUID victim, String weapon, long date, String victimName) {
        this.killer = new Killer(killer, null);
        this.victim = new Victim(victim, victimName);
        this.weapon = weapon;
        this.date = date;
        this.server = new ServerIdentifier(null, "");
    }

    @Deprecated
    public PlayerKill(UUID killer, UUID victim, String weapon, long date, String victimName, String killerName) {
        this.killer = new Killer(killer, killerName);
        this.victim = new Victim(victim, victimName);
        this.weapon = weapon;
        this.date = date;
        this.server = new ServerIdentifier(null, "");
    }

    public PlayerKill(Killer killer, Victim victim, ServerIdentifier server, String weapon, long date) {
        this.killer = killer;
        this.victim = victim;
        this.weapon = weapon;
        this.date = date;
        this.server = server;
    }

    @Deprecated
    public UUID getKillerUUID() {
        return killer.getUuid();
    }

    @Deprecated
    public UUID getVictimUUID() {
        return victim.getUuid();
    }

    @Deprecated
    public Optional<String> getVictimName() {
        return Optional.ofNullable(victim.getName());
    }

    @Deprecated
    public Optional<String> getKillerName() {
        return Optional.ofNullable(killer.getName());
    }

    public Killer getKiller() {
        return killer;
    }

    public Victim getVictim() {
        return victim;
    }

    public ServerIdentifier getServer() {
        return server;
    }

    @Override
    public long getDate() {
        return date;
    }

    /**
     * Get the Weapon used as string.
     *
     * @return For example DIAMOND_SWORD
     */
    public String getWeapon() {
        return weapon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerKill that = (PlayerKill) o;
        return date == that.date &&
                Objects.equals(killer, that.killer) &&
                Objects.equals(victim, that.victim) &&
                Objects.equals(server, that.server) &&
                Objects.equals(weapon, that.weapon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(killer, victim, server, date, weapon);
    }

    @Override
    public String toString() {
        return "PlayerKill{" +
                "killer=" + killer + ", " +
                "victim=" + victim + ", " +
                "server=" + server + ", " +
                "date=" + date + ", " +
                "weapon='" + weapon + "'}";
    }

    public boolean isSelfKill() {
        return killer.isSame(victim);
    }

    public boolean isNotSelfKill() {
        return !isSelfKill();
    }

    public String toJson() {
        return "{\"killer\": " + killer.toJson() + "," +
                "  \"victim\": " + victim.toJson() + "," +
                "  \"server\": " + server.toJson() + "," +
                "  \"weapon\": \"" + weapon + "\"," +
                "  \"date\": " + date +
                "}";
    }

    public static class Victim extends PlayerIdentifier {
        public Victim(UUID uuid, String name) {
            super(uuid, name);
        }
    }

    public static class Killer extends PlayerIdentifier {
        public Killer(UUID uuid, String name) {
            super(uuid, name);
        }
    }
}
