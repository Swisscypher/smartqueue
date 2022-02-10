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
import ch.swisscypher.smartqueue.bungee.exception.NoPermissionException;
import ch.swisscypher.smartqueue.bungee.exception.PlayerNotInQueueException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SmartQueue {

    private final TreeSet<SmartQueueEntry<ProxiedPlayer>> internalQueue = new TreeSet<>();
    private final HashMap<ProxiedPlayer, SmartQueueEntry<ProxiedPlayer>> entries = new HashMap<>();
    private final String regex;
    private final String name;
    private final Pattern pattern;
    private final ServerInfo destination;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition pollable = lock.newCondition();
    private final Thread thread;
    private final boolean needPriority;
    private int waiting = 1000;
    private boolean enabled;

    public SmartQueue(String name, ServerInfo destination, int waiting, boolean needPriority) {
        this.name = name;
        this.regex = String.format("smartqueue\\.%s\\.priority\\.((\\+|-)?[0-9]+)", name);
        this.pattern = Pattern.compile(regex);
        this.destination = destination;
        this.enabled = true;
        this.waiting = waiting;
        this.needPriority = needPriority;
        this.thread = new Thread(process);
        this.thread.start();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if(enabled) {
            lock.lock();
            try {
                pollable.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Condition getPollable() {
        return pollable;
    }

    public int getAvailableSlots() {
        Semaphore sem = new Semaphore(0);

        AtomicInteger res = new AtomicInteger(-1);

        destination.ping((result, error) -> {
            if(error != null) {
                sem.release();
                return;
            }

            res.set(result.getPlayers().getMax() - result.getPlayers().getOnline());
            sem.release();
        });

        try {
            return sem.tryAcquire(ProxyServer.getInstance().getConfig().getRemotePingTimeout(), TimeUnit.MILLISECONDS) ? res.get() : -1;
        } catch (InterruptedException e) {
            return -1;
        }
    }

    public boolean hasPlayers() {
        return !entries.isEmpty();
    }

    private void updateRanking() {
        AtomicInteger pos = new AtomicInteger(1);
        internalQueue.forEach(sqe -> sqe.setPosition(pos.getAndIncrement()));
        internalQueue.parallelStream().forEach(sqe -> {
            sqe.getEntry().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new TextComponent(Config.getInstance().getLabel("queue-position", name, sqe.getPosition(), internalQueue.size()))
            );
        });
    }

    synchronized public void sendFirstPlayer() {
        Semaphore sem = new Semaphore(0);
        SmartQueueEntry<ProxiedPlayer> player = internalQueue.first();

        if(player == null) {
            return;
        }

        if(player.getEntry().getServer().getInfo().equals(destination)) {   // This check is performed because of the obfuscator
            entries.remove(internalQueue.pollFirst().getEntry());
            updateRanking();
            return;
        }

        player.getEntry().connect(destination, (result, error) -> {
            if(error != null || !result) {
                sem.release();
                return;
            }

            entries.remove(internalQueue.pollFirst().getEntry());
            updateRanking();
            sem.release();
        });
        try {
            sem.acquire();
        } catch (InterruptedException ignored) {

        }
    }

    public ReentrantLock getLock() {
        return lock;
    }

    synchronized public void addPlayer(ProxiedPlayer player) throws NoPermissionException {

        if(player.getServer().getInfo().equals(destination))
            return;

        Collection<String> permissions = player.getPermissions();

        Integer priority = permissions.stream().filter(s -> pattern.matcher(s).matches())
                .map(s -> {
                    Matcher m = pattern.matcher(s);
                    if (m.find()) {
                        return Integer.parseInt(m.group(1));
                    } else {
                        return 0; // Never produce
                    }
                })
                .max(Integer::compareTo).orElse(null);

        if(priority == null) {
            if(needPriority) {
                throw new NoPermissionException();
            } else {
                priority = 0;
            }
        }

        addPlayerWithCustomPriority(player, priority);
    }

    synchronized public void addPlayerWithCustomPriority(ProxiedPlayer player, int priority) {
        if(!isPlayerInQueue(player)) {
            SmartQueueEntry<ProxiedPlayer> sqePlayer = new SmartQueueEntry<>(player, priority, internalQueue.size());
            internalQueue.add(sqePlayer);
            entries.put(player, sqePlayer);
            updateRanking();

            lock.lock();
            try {
                pollable.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    synchronized public void removePlayer(ProxiedPlayer player) {
        if(isPlayerInQueue(player)) {
            SmartQueueEntry<ProxiedPlayer> sqePlayer = entries.get(player);

            internalQueue.remove(sqePlayer);
            entries.remove(player);
            updateRanking();
        }
    }

    synchronized public boolean isPlayerInQueue(ProxiedPlayer player) {
        return entries.containsKey(player);
    }

    synchronized public long getPlayerPositionInQueue(ProxiedPlayer player) throws PlayerNotInQueueException {
        if(!isPlayerInQueue(player)) {
            throw new PlayerNotInQueueException();
        }

        return entries.get(player).getPosition();
    }

    synchronized public ArrayList<UUID> getPlayers() {
        return entries.keySet().stream().map(ProxiedPlayer::getUniqueId).collect(Collectors.toCollection(ArrayList::new));
    }

    Runnable process = () -> {
        main: while(true) {
            lock.lock();
            try {
                while(getAvailableSlots() <= 0 || entries.isEmpty() || !enabled) {
                    pollable.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }

            while(getAvailableSlots() > 0 && hasPlayers() && enabled) {
                sendFirstPlayer();
                try {
                    Thread.sleep(waiting);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break main;
                }
            }
        }
    };

    public ServerInfo getDestination() {
        return destination;
    }

    public void stop() {
        thread.interrupt();
    }
}
