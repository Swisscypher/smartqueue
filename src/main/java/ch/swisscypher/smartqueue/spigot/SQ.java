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

import ch.swisscypher.smartqueue.common.constant.Channel;
import ch.swisscypher.smartqueue.common.struct.Message;
import ch.swisscypher.smartqueue.common.util.ByteSerializer;
import ch.swisscypher.smartqueue.spigot.api.SmartQueue;
import ch.swisscypher.smartqueue.spigot.exception.NoPlayerException;
import ch.swisscypher.smartqueue.spigot.exception.TimeoutException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SQ extends SmartQueue {

    private final AtomicLong ID_GENERATOR = new AtomicLong();
    private static final long TIMEOUT = 1000;
    private static SQ instance;

    @Override
    public void addPlayer(Player player, String queue) {
        voidMethod("addPlayer", player.getUniqueId(), queue);
    }

    @Override
    public void removePlayerFromQueue(Player player, String queue) {
        voidMethod("removePlayerFromQueue", player.getUniqueId(), queue);
    }

    @Override
    public void removePlayerFromAllQueue(Player player) {
        voidMethod("removePlayerFromAllQueue", player.getUniqueId());
    }

    @Override
    public void setQueueStatus(String queue, boolean status) {
        voidMethod("setQueueStatus", queue, status);
    }

    @Override
    public CompletableFuture<Optional<Boolean>> getQueueStatus(String queue) {

        CompletableFuture<Optional<Boolean>> c = new CompletableFuture<>();

        MainSpigot.getInstance().getThreadPool().execute(() -> {
            c.complete(Optional.ofNullable(returnMethod("getQueueStatus", queue)));
        });

        return c;
    }

    @Override
    public CompletableFuture<Boolean> isPlayerInQueue(Player player, String queue) {

        CompletableFuture<Boolean> c = new CompletableFuture<>();

        MainSpigot.getInstance().getThreadPool().execute(() -> {
            c.complete(returnMethod("isPlayerInQueue", player.getUniqueId(), queue));
        });

        return c;
    }

    @Override
    public CompletableFuture<Optional<Long>> getPlayerPositionInQueue(Player player, String queue) {

        CompletableFuture<Optional<Long>> c = new CompletableFuture<>();

        MainSpigot.getInstance().getThreadPool().execute(() -> {
            c.complete(Optional.ofNullable(returnMethod("getPlayerPositionInQueue", player.getUniqueId(), queue)));
        });

        return c;
    }

    @Override
    public CompletableFuture<Optional<List<OfflinePlayer>>> getPlayersInQueue(String queue) {

        CompletableFuture<Optional<List<OfflinePlayer>>> c = new CompletableFuture<>();

        MainSpigot.getInstance().getThreadPool().execute(() -> {

            List<UUID> uuids = returnMethod("getPlayersInQueue", queue);

            if(uuids != null) {
                List<OfflinePlayer> players = uuids.stream().map(uuid -> {
                    OfflinePlayer p = Bukkit.getServer().getPlayer(uuid);

                    if(p == null) {
                        p = new SmartQueueOfflinePlayer(uuid);
                    }

                    return p;
                }).collect(Collectors.toList());

                c.complete(Optional.of(players));
            } else {
                c.complete(Optional.empty());
            }
        });

        return c;
    }

    private void voidMethod(String name, Serializable... params) {
        Message msg = new Message(ID_GENERATOR.incrementAndGet(), name, params);
        try {
            sendPluginMessage(MainSpigot.getInstance(), Channel.METHODS, ByteSerializer.toBytes(msg));
        } catch (NoPlayerException ignored) {

        }
    }

    private <T extends Serializable> T returnMethod(String name, Serializable... params) {

        Semaphore sem = new Semaphore(0);

        long id = ID_GENERATOR.incrementAndGet();

        Message isPlayerInQueue = new Message(id, name, params);

        MessageManager.getInstance().addSem(id, sem);

        try {
            sendPluginMessage(MainSpigot.getInstance(), Channel.METHODS, ByteSerializer.toBytes(isPlayerInQueue));
        } catch (NoPlayerException e) {
            return null;
        }

        try {
            if(!sem.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException();
            }

            Message msg = MessageManager.getInstance().getMessage(id);

            MessageManager.getInstance().clear(id);

            return msg.getReturnedValue();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void sendPluginMessage(Plugin source, String channel, byte[] message) throws NoPlayerException {
        Optional<? extends Player> p = Bukkit.getOnlinePlayers().stream().findFirst();

        if(!p.isPresent())
            throw new NoPlayerException();

        p.get().sendPluginMessage(source, channel, message);
    }

    private SQ() { }

    public static SQ getInstance() {
        if(instance == null) {
            instance = new SQ();
        }

        return instance;
    }
}
