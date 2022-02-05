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

import ch.swisscypher.smartqueue.bungee.MainBungee;
import ch.swisscypher.smartqueue.common.constant.Channel;
import ch.swisscypher.smartqueue.common.struct.Message;
import ch.swisscypher.smartqueue.common.util.ByteSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class MessageManager implements PluginMessageListener {
    private Map<Long, Message> messages = new HashMap<>();
    private Map<Long, Semaphore> semaphores = new HashMap<>();

    private final static MessageManager instance = new MessageManager();

    public static MessageManager getInstance() {
        return instance;
    }

    private MessageManager() { }

    synchronized private void accept(Message m) {
        messages.put(m.getId(), m);
        semaphores.get(m.getId()).release();
    }

    synchronized public void addSem(long id, Semaphore sem) {
        semaphores.put(id, sem);
    }

    synchronized public Message getMessage(long id) {
        return messages.get(id);
    }

    synchronized public void clear(long id) {
        messages.remove(id);
        semaphores.remove(id);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        MainSpigot.getInstance().getThreadPool().execute(() -> {
            if(channel.equals(Channel.METHODS)) {
                accept(ByteSerializer.fromByte(message));
            }
        });
    }
}
