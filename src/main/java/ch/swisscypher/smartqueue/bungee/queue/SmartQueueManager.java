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

import ch.swisscypher.smartqueue.bungee.config.Config;
import ch.swisscypher.smartqueue.bungee.exception.DestinationNotValidException;
import ch.swisscypher.smartqueue.bungee.exception.NoPermissionException;
import ch.swisscypher.smartqueue.bungee.exception.PlayerNotInQueueException;
import ch.swisscypher.smartqueue.bungee.exception.QueueNotExistsException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SmartQueueManager {

    private HashMap<String, SmartQueue> sqs = new HashMap<>();
    private HashMap<ServerInfo, SmartQueue> sis = new HashMap<>();

    private static SmartQueueManager instance;

    private SmartQueueManager() { }

    public static SmartQueueManager getInstance() {
        if(instance == null) {
            instance = new SmartQueueManager();
        }

        return instance;
    }

    public void createSmartQueue(String name, ServerInfo destination, int waiting, boolean needPriority) {
        if(!sqs.containsKey(name)) {
            sqs.put(name, new SmartQueue(name, destination, waiting, needPriority));
            sis.put(destination, sqs.get(name));
        }
    }

    public void destroyAll() {
        sqs.forEach((k, sq) -> sq.stop());
        sqs.clear();
        sis.clear();
    }

    public void addPlayerToQueueWithCustomPriority(String name, ProxiedPlayer player, int priority) throws QueueNotExistsException {
        if(!sqs.containsKey(name)) {
            throw new QueueNotExistsException(name);
        }
        sqs.get(name).addPlayerWithCustomPriority(player, priority);
    }

    public void addPlayerToQueue(String name, ProxiedPlayer player) throws QueueNotExistsException {
        if(!sqs.containsKey(name)) {
            throw new QueueNotExistsException(name);
        }
        try {
            sqs.get(name).addPlayer(player);
        } catch (NoPermissionException e) {
            player.sendMessage(new TextComponent(Config.getInstance().getLabel("cannot-join-queue", name)));
        }
    }

    public void removePlayerFromAllQueue(ProxiedPlayer player) {
        sqs.forEach((s, sq) -> sq.removePlayer(player));
    }

    public void removePlayerFromQueue(String name, ProxiedPlayer player) throws QueueNotExistsException {
        if(!sqs.containsKey(name)) {
            throw new QueueNotExistsException(name);
        }
        sqs.get(name).removePlayer(player);
    }

    public boolean isPlayerInQueue(String name, ProxiedPlayer player) {
        if(!sqs.containsKey(name)) {
            return false;
        }

        return sqs.get(name).isPlayerInQueue(player);
    }

    public long getPlayerPositionInQueue(String name, ProxiedPlayer player) throws QueueNotExistsException, PlayerNotInQueueException {
        if(!sqs.containsKey(name)) {
            throw new QueueNotExistsException(name);
        }

        return sqs.get(name).getPlayerPositionInQueue(player);
    }

    public ArrayList<UUID> getPlayersInQueue(String name) throws QueueNotExistsException {
        if(!sqs.containsKey(name)) {
            throw new QueueNotExistsException(name);
        }

        return sqs.get(name).getPlayers();
    }

    public void playerSwitchServer(ServerInfo from) {
        if(from == null || !sis.containsKey(from)) {
            return;
        }

        sis.get(from).getLock().lock();
        try {
            sis.get(from).getPollable().signal();
        } finally {
            sis.get(from).getLock().unlock();
        }
    }

    public void stop() {
        sqs.forEach((k, sq) -> sq.stop());
    }

    public void setEnabled(String name, boolean value) throws QueueNotExistsException {
        if(!sqs.containsKey(name)) {
            throw new QueueNotExistsException(name);
        }
        sqs.get(name).setEnabled(value);
    }

    public boolean isEnabled(String name) throws QueueNotExistsException {
        if(!sqs.containsKey(name)) {
            throw new QueueNotExistsException(name);
        }

        return sqs.get(name).isEnabled();
    }

    public void unstuck(String name) throws QueueNotExistsException {
        if(!sqs.containsKey(name)) {
            throw new QueueNotExistsException(name);
        }

        sqs.get(name).getLock().lock();
        try {
            sqs.get(name).getPollable().signal();
        } finally {
            sqs.get(name).getLock().unlock();
        }
    }
}
