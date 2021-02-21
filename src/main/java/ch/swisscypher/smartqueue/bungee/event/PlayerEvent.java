/*
 * SmartQueue: Minecraft plugin implementing a queue system.
 * Copyright (C) 2021 Zayceur (dev@zayceur.ch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
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

import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerEvent implements Listener {

    @EventHandler
    public void onPlayerLeave(ServerDisconnectEvent e) {
        new Thread(() -> {
            try {
                Thread.sleep(100);  // Waiting on bungee to update the values
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
            }
            SmartQueueManager.getInstance().playerSwitchServer(e.getTarget());
            SmartQueueManager.getInstance().removePlayerFromAllQueue(e.getPlayer());
        }).start();
    }
}
