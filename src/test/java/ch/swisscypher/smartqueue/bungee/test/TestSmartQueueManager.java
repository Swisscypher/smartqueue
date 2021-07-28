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

package ch.swisscypher.smartqueue.bungee.test;

import ch.swisscypher.smartqueue.bungee.config.Config;
import ch.swisscypher.smartqueue.bungee.exception.QueueNotExistsException;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueue;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueEntry;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.*;

public class TestSmartQueueManager {
    private final SmartQueueManager manager = SmartQueueManager.getInstance();

    private static final Random random = new Random();

    private static final ServerPing ping = new ServerPing();
    private static final Config config = Mockito.mock(Config.class);
    private static final ProxiedPlayer player = Mockito.mock(ProxiedPlayer.class);
    private static final ServerInfo info = Mockito.mock(ServerInfo.class);
    private static final Server server = Mockito.mock(Server.class);

    private static final String name = UUID.randomUUID().toString();
    private static final ServerInfo destination = Mockito.mock(ServerInfo.class);
    private static final int waiting = random.nextInt(1000);
    private static final boolean needPriority = true;

    private static final int priority = random.nextInt(10);

    @BeforeAll
    static void init() {
        // TODO: Fix InterruptException when SmartQueue#getAvailableSlots is called.

        ping.setPlayers(new ServerPing.Players(10, 5 + random.nextInt(5), new ServerPing.PlayerInfo[0]));

        Mockito.doReturn(info).when(server).getInfo();
        Mockito.doReturn(server).when(player).getServer();
        Mockito.doReturn(Collections.singletonList("smartqueue." + name + ".priority." + priority)).when(player).getPermissions();

        Mockito.doAnswer(invocation -> {
            Callback<ServerPing> argumentAt = invocation.getArgumentAt(0, Callback.class);

            argumentAt.done(ping, null);
            return null;
        }).when(destination).ping(Mockito.any());

        Mockito.doAnswer(invocation -> {
            Callback<Boolean> argumentAt = invocation.getArgumentAt(1, Callback.class);

            argumentAt.done(true, null);
            return null;
        }).when(player).connect(Mockito.any(ServerInfo.class), Mockito.any(Callback.class));

        Mockito.doReturn("").when(config).getLabel(Mockito.anyString());

        try {
            Field instance = Config.class.getDeclaredField("instance");

            instance.setAccessible(true);
            instance.set(null, config);
            instance.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void initTest() {
        manager.createSmartQueue(name, destination, waiting, needPriority);
    }

    @AfterEach
    void resetTest() {
        manager.destroyAll();
    }

    @Test
    public void testCreateSmartQueue() {
        try {
            Field sqsField = SmartQueueManager.class.getDeclaredField("sqs");

            sqsField.setAccessible(true);
            Map<String, SmartQueue> sqs = (Map<String, SmartQueue>) sqsField.get(manager);
            sqsField.setAccessible(false);

            SmartQueue queue = sqs.get(name);

            Field nameField = SmartQueue.class.getDeclaredField("name");
            Field waitingField = SmartQueue.class.getDeclaredField("waiting");
            Field needPriorityField = SmartQueue.class.getDeclaredField("needPriority");

            nameField.setAccessible(true);
            waitingField.setAccessible(true);
            needPriorityField.setAccessible(true);

            String queueName = (String) nameField.get(queue);
            ServerInfo queueDestination = queue.getDestination();
            int queueWaiting = (int) waitingField.get(queue);
            boolean queueNeedPriority = (boolean) needPriorityField.get(queue);

            nameField.setAccessible(false);
            waitingField.setAccessible(false);
            needPriorityField.setAccessible(false);

            Assertions.assertEquals(name, queueName);
            Assertions.assertEquals(destination, queueDestination);
            Assertions.assertEquals(waiting, queueWaiting);
            Assertions.assertEquals(needPriority, queueNeedPriority);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testDestroyAll() {
        manager.destroyAll();

        try {
            Field sqsField = SmartQueueManager.class.getDeclaredField("sqs");
            Field sisField = SmartQueueManager.class.getDeclaredField("sis");

            sqsField.setAccessible(true);
            sisField.setAccessible(true);
            Map<String, SmartQueue> sqs = (Map<String, SmartQueue>) sqsField.get(manager);
            Map<ServerInfo, SmartQueue> sis = (Map<ServerInfo, SmartQueue>) sqsField.get(manager);
            sisField.setAccessible(false);
            sqsField.setAccessible(false);

            Assertions.assertTrue(sqs.isEmpty());
            Assertions.assertTrue(sis.isEmpty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testAddPlayerToQueueWithCustomPriority() {
        int priority = random.nextInt(100);

        try {
            manager.addPlayerToQueueWithCustomPriority(name, player, priority);

            Field sqsField = SmartQueueManager.class.getDeclaredField("sqs");

            sqsField.setAccessible(true);
            Map<String, SmartQueue> sqs = (Map<String, SmartQueue>) sqsField.get(manager);
            sqsField.setAccessible(false);

            SmartQueue queue = sqs.get(name);
            Field internalQueueField = SmartQueue.class.getDeclaredField("entries");

            internalQueueField.setAccessible(true);
            HashMap<ProxiedPlayer, SmartQueueEntry<ProxiedPlayer>> internalQueue = (HashMap<ProxiedPlayer, SmartQueueEntry<ProxiedPlayer>>) internalQueueField.get(queue);
            internalQueueField.setAccessible(false);

            Field priorityField = SmartQueueEntry.class.getDeclaredField("priority");

            priorityField.setAccessible(true);
            long internalPriority = (long) priorityField.get(internalQueue.get(player));
            priorityField.setAccessible(false);

            Assertions.assertEquals(priority, internalPriority);
        } catch (QueueNotExistsException | NoSuchFieldException | IllegalAccessException e) {
            Assertions.fail(e);
        }

        Assertions.assertThrows(QueueNotExistsException.class, () -> manager.addPlayerToQueueWithCustomPriority(UUID.randomUUID().toString(), player, priority));
    }

    @Test
    public void testAddPlayerToQueue() {
        try {
            manager.addPlayerToQueue(name, player);
            Field sqsField = SmartQueueManager.class.getDeclaredField("sqs");

            sqsField.setAccessible(true);
            Map<String, SmartQueue> sqs = (Map<String, SmartQueue>) sqsField.get(manager);
            sqsField.setAccessible(false);

            SmartQueue queue = sqs.get(name);
            Field internalQueueField = SmartQueue.class.getDeclaredField("entries");

            internalQueueField.setAccessible(true);
            HashMap<ProxiedPlayer, SmartQueueEntry<ProxiedPlayer>> internalQueue = (HashMap<ProxiedPlayer, SmartQueueEntry<ProxiedPlayer>>) internalQueueField.get(queue);
            internalQueueField.setAccessible(false);

            Field priorityField = SmartQueueEntry.class.getDeclaredField("priority");

            priorityField.setAccessible(true);
            long internalPriority = (long) priorityField.get(internalQueue.get(player));
            priorityField.setAccessible(false);

            ProxiedPlayer anotherPlayer = Mockito.mock(ProxiedPlayer.class);
            Mockito.doReturn(server).when(anotherPlayer).getServer();
            Mockito.doReturn(Collections.emptyList()).when(anotherPlayer).getPermissions();

            manager.addPlayerToQueue(name, anotherPlayer);

            Mockito.verify(anotherPlayer).sendMessage(Mockito.any(BaseComponent.class));

            Assertions.assertEquals(priority, internalPriority);
        } catch (QueueNotExistsException | NoSuchFieldException | IllegalAccessException e) {
            Assertions.fail(e);
        }

        Assertions.assertThrows(QueueNotExistsException.class, () -> manager.addPlayerToQueue(UUID.randomUUID().toString(), player));
    }

    @Test
    public void testIsPlayerInQueue() {
        try {
            manager.addPlayerToQueue(name, player);
            Assertions.assertTrue(manager.isPlayerInQueue(name, player));
            Assertions.assertFalse(manager.isPlayerInQueue(name, Mockito.mock(ProxiedPlayer.class)));
            Assertions.assertFalse(manager.isPlayerInQueue(UUID.randomUUID().toString(), player));
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }
    }
}
