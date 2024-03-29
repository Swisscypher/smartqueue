/*
 * SmartQueue: Minecraft plugin implementing a queue system.
 * Copyright (C) 2021-2022 SwissCypher (contact@swisscypher.ch)
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
import ch.swisscypher.smartqueue.bungee.exception.PlayerNotInQueueException;
import ch.swisscypher.smartqueue.bungee.exception.QueueNotExistsException;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueue;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueEntry;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ProxyServer.class })
class TestSmartQueueManager {
    private final SmartQueueManager manager = SmartQueueManager.getInstance();

    private static final Random random = new Random();

    private static final ProxyServer proxyServer = mock(ProxyServer.class);
    private static final ProxyConfig proxyConfig = mock(ProxyConfig.class);

    private static final ServerPing ping = new ServerPing();
    private static final Config config = mock(Config.class);
    private static final ProxiedPlayer player = mock(ProxiedPlayer.class);
    private static final ServerInfo info = mock(ServerInfo.class);
    private static final Server server = mock(Server.class);

    private static final String name = UUID.randomUUID().toString();
    private static final Server destinationServer = mock(Server.class);
    private static final ServerInfo destination = mock(ServerInfo.class);
    private static final int waiting = random.nextInt(1000);
    private static final boolean needPriority = true;

    private static final int priority = random.nextInt(10);

    @BeforeAll
    static void init() {
        // TODO: Fix InterruptException when SmartQueue#getAvailableSlots is called.

        doReturn(5000).when(proxyConfig).getRemotePingTimeout();
        doReturn(5000).when(proxyConfig).getServerConnectTimeout();
        doReturn(proxyConfig).when(proxyServer).getConfig();

        Whitebox.setInternalState(ProxyServer.class, "instance", proxyServer);

        ping.setPlayers(new ServerPing.Players(10, 5 + random.nextInt(5), new ServerPing.PlayerInfo[0]));

        doReturn(destination).when(destinationServer).getInfo();

        doReturn(info).when(server).getInfo();
        doReturn(server).when(player).getServer();
        doReturn(Collections.singletonList("smartqueue." + name + ".priority." + priority)).when(player).getPermissions();

        doAnswer(invocation -> {
            Callback<ServerPing> argumentAt = invocation.getArgument(0, Callback.class);

            argumentAt.done(ping, null);
            return null;
        }).when(destination).ping(any());

        doAnswer(invocation -> {
            ServerInfo info = invocation.getArgument(0);
            Callback<Boolean> callback = invocation.getArgument(1, Callback.class);

            if(info.equals(destination)) {
                callback.done(false, new Exception());
                return null;
            }

            callback.done(true, null);
            return null;
        }).when(player).connect(any(ServerInfo.class), any(Callback.class));

        doReturn("").when(config).getLabel(anyString());

        Whitebox.setInternalState(Config.class, "instance", config);
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
    void testCreateSmartQueue() {
        Map<String, SmartQueue> sqs = Whitebox.getInternalState(manager, "sqs");

        SmartQueue queue = sqs.get(name);

        String queueName = Whitebox.getInternalState(queue, "name");
        ServerInfo queueDestination = queue.getDestination();
        int queueWaiting = Whitebox.getInternalState(queue, "waiting");
        boolean queueNeedPriority = Whitebox.getInternalState(queue, "needPriority");

        Assertions.assertEquals(name, queueName);
        Assertions.assertEquals(destination, queueDestination);
        Assertions.assertEquals(waiting, queueWaiting);
        Assertions.assertEquals(needPriority, queueNeedPriority);
    }

    @Test
    void testDestroyAll() {
        manager.destroyAll();

        Map<String, SmartQueue> sqs = Whitebox.getInternalState(manager, "sqs");
        Map<ServerInfo, SmartQueue> sis = Whitebox.getInternalState(manager, "sis");

        Assertions.assertTrue(sqs.isEmpty());
        Assertions.assertTrue(sis.isEmpty());
    }

    @Test
    void testAddPlayerToQueueWithCustomPriority() {
        int priority = random.nextInt(100);

        try {
            manager.addPlayerToQueueWithCustomPriority(name, player, priority);

            HashMap<String, SmartQueue> sqs = Whitebox.getInternalState(manager, "sqs");
            SmartQueue queue = sqs.get(name);

            HashMap<ProxiedPlayer, SmartQueueEntry<ProxiedPlayer>> entries = Whitebox.getInternalState(queue, "entries");
            long internalPriority = Whitebox.getInternalState(entries.get(player), "priority");

            Assertions.assertEquals(priority, internalPriority);
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }

        Assertions.assertThrows(QueueNotExistsException.class, () -> manager.addPlayerToQueueWithCustomPriority(UUID.randomUUID().toString(), player, priority));
    }

    @Test
    void testAddPlayerToQueue() {
        try {
            Map<String, SmartQueue> sqs = Whitebox.getInternalState(manager, "sqs");
            SmartQueue queue = sqs.get(name);

            ProxiedPlayer anotherPlayer = mock(ProxiedPlayer.class);
            doReturn(server).when(anotherPlayer).getServer();
            doReturn(Collections.singletonList("smartqueue." + name + ".priority." + priority)).when(anotherPlayer).getPermissions();
            doAnswer(invocation -> {
                Callback<Boolean> callback = invocation.getArgument(1);
                callback.done(true, null);
                return null;
            }).when(anotherPlayer).connect(any(ServerInfo.class), any(Callback.class));
            manager.addPlayerToQueue(name, anotherPlayer);

            manager.addPlayerToQueue(name, player);

            HashMap<ProxiedPlayer, SmartQueueEntry<ProxiedPlayer>> entries = Whitebox.getInternalState(queue, "entries");
            long internalPriority = Whitebox.getInternalState(entries.get(player), "priority");
            Assertions.assertEquals(priority, internalPriority);

            ProxiedPlayer connectedPlayer = mock(ProxiedPlayer.class);
            doReturn(server).when(connectedPlayer).getServer();
            doReturn(Collections.emptyList()).when(connectedPlayer).getPermissions();
            manager.addPlayerToQueue(name, connectedPlayer);

            verify(connectedPlayer).sendMessage(any(BaseComponent.class));
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }

        Assertions.assertThrows(QueueNotExistsException.class, () -> manager.addPlayerToQueue(UUID.randomUUID().toString(), player));
    }

    @Test
    void testIsPlayerInQueue() {
        try {
            manager.addPlayerToQueue(name, player);

            Assertions.assertTrue(manager.isPlayerInQueue(name, player));
            Assertions.assertFalse(manager.isPlayerInQueue(name, mock(ProxiedPlayer.class)));
            Assertions.assertFalse(manager.isPlayerInQueue(UUID.randomUUID().toString(), player));
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testRemovePlayerFromQueue() {
        try {
            Assertions.assertThrows(QueueNotExistsException.class, () -> manager.removePlayerFromQueue(UUID.randomUUID().toString(), player));
            manager.setEnabled(name, false);

            manager.addPlayerToQueue(name, player);
            Assertions.assertDoesNotThrow(() -> manager.getPlayerPositionInQueue(name, player));

            manager.removePlayerFromQueue(name, player);
            Assertions.assertThrows(PlayerNotInQueueException.class, () -> manager.getPlayerPositionInQueue(name, player));
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testRemovePlayerFromAllQueue() {
        manager.createSmartQueue("test", mock(ServerInfo.class), waiting, false);

        try {
            manager.setEnabled(name, false);
            manager.setEnabled("test", false);

            manager.addPlayerToQueue(name, player);
            manager.addPlayerToQueue("test", player);

            Assertions.assertDoesNotThrow(() -> manager.getPlayerPositionInQueue(name, player));
            Assertions.assertDoesNotThrow(() -> manager.getPlayerPositionInQueue("test", player));

            manager.removePlayerFromAllQueue(player);

            Assertions.assertThrows(PlayerNotInQueueException.class, () -> manager.getPlayerPositionInQueue(name, player));
            Assertions.assertThrows(PlayerNotInQueueException.class, () -> manager.getPlayerPositionInQueue("test", player));
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testGetPlayerPositionInQueue() {
        try {
            manager.addPlayerToQueue(name, player);
            Assertions.assertThrows(QueueNotExistsException.class, () -> SmartQueueManager.getInstance().getPlayerPositionInQueue(UUID.randomUUID().toString(), player));

            Map<String, SmartQueue> sqs = Whitebox.getInternalState(manager, "sqs");
            SmartQueue queue = sqs.get(name);

            Assertions.assertEquals(SmartQueueManager.getInstance().getPlayerPositionInQueue(name, player), queue.getPlayerPositionInQueue(player));
        } catch (QueueNotExistsException | PlayerNotInQueueException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testGetPlayersInQueue() {
        try {
            Assertions.assertThrows(QueueNotExistsException.class, () -> SmartQueueManager.getInstance().getPlayersInQueue(UUID.randomUUID().toString()));

            Map<String, SmartQueue> sqs = Whitebox.getInternalState(manager, "sqs");
            SmartQueue queue = sqs.get(name);

            manager.addPlayerToQueue(name, player);
            Assertions.assertEquals(SmartQueueManager.getInstance().getPlayersInQueue(name), queue.getPlayers());
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testPlayerSwitchServer() {
        Assertions.assertDoesNotThrow(() -> manager.playerSwitchServer(mock(ServerInfo.class)));
        Assertions.assertDoesNotThrow(() -> manager.playerSwitchServer(destination));
    }

    @Test
    void testStop() {
        Assertions.assertDoesNotThrow(manager::stop);
    }

    @Test
    void testSetEnabled() {
        Assertions.assertThrows(QueueNotExistsException.class, () -> SmartQueueManager.getInstance().setEnabled(UUID.randomUUID().toString(), true));

        try {
            boolean enabled = random.nextBoolean();
            SmartQueueManager.getInstance().setEnabled(name, enabled);
            Map<String, SmartQueue> sqs = Whitebox.getInternalState(manager, "sqs");
            SmartQueue smartQueue = sqs.get(name);

            Assertions.assertEquals(enabled, smartQueue.isEnabled());
            SmartQueueManager.getInstance().setEnabled(name, true);
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testIsEnabled() {
        Assertions.assertThrows(QueueNotExistsException.class, () -> SmartQueueManager.getInstance().isEnabled(UUID.randomUUID().toString()));

        Map<String, SmartQueue> sqs = Whitebox.getInternalState(manager, "sqs");
        SmartQueue smartQueue = sqs.get(name);

        try {
            Assertions.assertEquals(SmartQueueManager.getInstance().isEnabled(name), smartQueue.isEnabled());
        } catch (QueueNotExistsException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testUnstuck() {
        Assertions.assertThrows(QueueNotExistsException.class, () -> SmartQueueManager.getInstance().unstuck(UUID.randomUUID().toString()));

        Assertions.assertDoesNotThrow(() -> SmartQueueManager.getInstance().unstuck(name));
    }
}
