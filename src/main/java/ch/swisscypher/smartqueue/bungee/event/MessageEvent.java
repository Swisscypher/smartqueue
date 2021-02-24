/*
 * SmartQueue: Minecraft plugin implementing a queue system.
 * Copyright (C) 2021 Zayceur (dev@zayceur.ch)
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

import ch.swisscypher.smartqueue.bungee.config.Config;
import ch.swisscypher.smartqueue.bungee.exception.PlayerNotInQueueException;
import ch.swisscypher.smartqueue.bungee.exception.QueueNotExistsException;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import ch.swisscypher.smartqueue.common.constant.Channel;
import ch.swisscypher.smartqueue.common.struct.Message;
import ch.swisscypher.smartqueue.common.util.ByteSerializer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class MessageEvent implements Listener {

    @EventHandler
    public void onMessage(PluginMessageEvent e) {
        new Thread(() -> {
            if(e.getTag().equals(Channel.METHODS)) {
                Message msg = ByteSerializer.fromByte(e.getData());

                UUID uuid;
                String queue;
                ProxiedPlayer player;

                ServerInfo srv = ProxyServer.getInstance().getServers().values().stream().filter(si -> si.getSocketAddress().equals(e.getSender().getSocketAddress())).findFirst().get();

                if(msg != null) {
                    switch (msg.getMethod()) {
                        case "addPlayer":   // void
                            uuid = (UUID) msg.getParams()[0];
                            queue = (String) msg.getParams()[1];

                            player = ProxyServer.getInstance().getPlayer(uuid);

                            if (player != null) {
                                try {
                                    SmartQueueManager.getInstance().addPlayerToQueue(queue, player);
                                } catch (QueueNotExistsException queueNotExistsException) {
                                    player.sendMessage(new TextComponent(String.format(Config.getInstance().lang.getConfiguration().getString("queue-non-existent"), queue)));
                                }
                            }

                            break;

                        case "removePlayerFromQueue":   // void
                            uuid = (UUID) msg.getParams()[0];
                            queue = (String) msg.getParams()[1];

                            player = ProxyServer.getInstance().getPlayer(uuid);

                            if (player != null) {
                                try {
                                    SmartQueueManager.getInstance().removePlayerFromQueue(queue, player);
                                } catch (QueueNotExistsException queueNotExistsException) {
                                    player.sendMessage(new TextComponent(String.format(Config.getInstance().lang.getConfiguration().getString("queue-non-existent"), queue)));
                                }
                            }
                            break;

                        case "removePlayerFromAllQueue":    // void
                            uuid = (UUID) msg.getParams()[0];

                            player = ProxyServer.getInstance().getPlayer(uuid);

                            if (player != null) {
                                SmartQueueManager.getInstance().removePlayerFromAllQueue(player);
                            }
                            break;

                        case "isPlayerInQueue": // boolean

                            uuid = (UUID)msg.getParams()[0];
                            queue = (String)msg.getParams()[1];

                            player = ProxyServer.getInstance().getPlayer(uuid);

                            if(player != null) {
                                msg.setReturnedValue(SmartQueueManager.getInstance().isPlayerInQueue(queue, player));
                            } else {
                                msg.setReturnedValue(false);
                            }

                            srv.sendData(Channel.METHODS, ByteSerializer.toBytes(msg));
                            break;

                        case "getPlayerPositionInQueue":    // long

                            uuid = (UUID)msg.getParams()[0];
                            queue = (String)msg.getParams()[1];

                            player = ProxyServer.getInstance().getPlayer(uuid);

                            if(player != null) {
                                try {
                                    msg.setReturnedValue(SmartQueueManager.getInstance().getPlayerPositionInQueue(queue, player));
                                } catch (QueueNotExistsException | PlayerNotInQueueException e2) {
                                    msg.setReturnedValue(null);
                                }
                            } else {
                                msg.setReturnedValue(null);
                            }

                            srv.sendData(Channel.METHODS, ByteSerializer.toBytes(msg));
                            break;

                        case "getPlayersInQueue":   // List<UUID>

                            queue = (String)msg.getParams()[0];

                            try {
                                msg.setReturnedValue(SmartQueueManager.getInstance().getPlayersInQueue(queue));
                            } catch (QueueNotExistsException queueNotExistsException) {
                                msg.setReturnedValue(null);
                            }

                            srv.sendData(Channel.METHODS, ByteSerializer.toBytes(msg));
                            break;
                        case "setQueueStatus":
                            queue = (String)msg.getParams()[0];
                            boolean status = (boolean)msg.getParams()[1];

                            try {
                                SmartQueueManager.getInstance().setEnabled(queue, status);
                            } catch (QueueNotExistsException ignored) {

                            }

                            break;

                        case "getQueueStatus":
                            queue = (String)msg.getParams()[0];

                            try {
                                msg.setReturnedValue(SmartQueueManager.getInstance().isEnabled(queue));
                            } catch (QueueNotExistsException queueNotExistsException) {
                                msg.setReturnedValue(null);
                            }

                            srv.sendData(Channel.METHODS, ByteSerializer.toBytes(msg));
                            break;
                    }
                }
            }
        }).start();
    }

}
