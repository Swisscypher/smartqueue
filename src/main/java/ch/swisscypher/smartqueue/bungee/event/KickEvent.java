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

package ch.swisscypher.smartqueue.bungee.event;

import ch.swisscypher.smartqueue.bungee.MainBungee;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueue;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class KickEvent implements Listener {
    @EventHandler
    public void onPlayerKickEvent(ServerKickEvent e) {
        MainBungee.getInstance().getThreadPool().execute(() -> {
            // Pause queue if a client cannot connect to the destination server
            if(e.getState() == ServerKickEvent.State.CONNECTING) {
                ServerInfo toServer = e.getKickedFrom();
                SmartQueue queue = SmartQueueManager.getInstance().getQueueFromServerInfo(toServer);
                if(queue != null) {
                    queue.setEnabled(false);
                    // Small hack to add player at the top of the queue
                    queue.addPlayerWithCustomPriority(e.getPlayer(), Integer.MAX_VALUE);
                    ProxyServer.getInstance().getLogger().warning(String.format(
                            "Queue %s is now paused because distant server is in whitelist or has connection issue",
                            queue.getName()
                    ));
                }
            }
        });
    }
}
