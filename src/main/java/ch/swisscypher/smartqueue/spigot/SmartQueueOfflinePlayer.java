/*
 * SmartQueue: Minecraft plugin implementing a queue system.
 * Copyright (C) 2021-2022 Zayceur (dev@zayceur.ch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.swisscypher.smartqueue.spigot;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class SmartQueueOfflinePlayer implements OfflinePlayer {

    private UUID uuid;

    public SmartQueueOfflinePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public String getName() {
        throw new NotImplementedException();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean isBanned() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isWhitelisted() {
        throw new NotImplementedException();
    }

    @Override
    public void setWhitelisted(boolean value) {
        throw new NotImplementedException();
    }

    @Override
    public Player getPlayer() {
        throw new NotImplementedException();
    }

    @Override
    public long getFirstPlayed() {
        throw new NotImplementedException();
    }

    @Override
    public long getLastPlayed() {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasPlayedBefore() {
        return false;
    }

    @Override
    public Location getBedSpawnLocation() {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, Object> serialize() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {
        throw new NotImplementedException();
    }
}
