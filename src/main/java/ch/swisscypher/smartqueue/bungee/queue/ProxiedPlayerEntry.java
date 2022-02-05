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

package ch.swisscypher.smartqueue.bungee.queue;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ProxiedPlayerEntry {

    private final ProxiedPlayer proxiedPlayer;

    public ProxiedPlayerEntry(ProxiedPlayer proxiedPlayer) {
        this.proxiedPlayer = proxiedPlayer;
    }

    public ProxiedPlayer getProxiedPlayer() {
        return proxiedPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxiedPlayerEntry that = (ProxiedPlayerEntry) o;

        return proxiedPlayer != null ?
                proxiedPlayer.getUniqueId().equals(that.proxiedPlayer.getUniqueId()) :
                that.proxiedPlayer == null;
    }

    @Override
    public int hashCode() {
        return proxiedPlayer != null ? proxiedPlayer.getUniqueId().hashCode() : 0;
    }
}
