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

import ch.swisscypher.smartqueue.bungee.queue.SmartQueue;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TestSmartQueueManager {
    private final SmartQueueManager manager = SmartQueueManager.getInstance();

    private final Random random = new Random();
    private static ServerInfo info = Mockito.mock(ServerInfo.class);

    @BeforeAll
    static void init() {
        // TODO: Fix InterruptException when SmartQueue#getAvailableSlots is called.

        Mockito.doAnswer(invocation -> {
            Callback<?> argumentAt = invocation.getArgumentAt(0, Callback.class);

            argumentAt.done(null, new Exception());
            return null;
        }).when(info).ping(Mockito.any());
    }

    @BeforeEach
    void initTest() {
        manager.createSmartQueue("test", info, random.nextInt(1000), random.nextBoolean());
    }

    @AfterEach
    void resetTest() {
        manager.destroyAll();
    }

    @Test
    public void testCreateSmartQueue() {
        String name = UUID.randomUUID().toString();
        ServerInfo destination = Mockito.mock(ServerInfo.class);
        int waiting = random.nextInt(1000);
        boolean needPriority = random.nextBoolean();

        Mockito.doAnswer(invocation -> {
            Callback<?> argumentAt = invocation.getArgumentAt(0, Callback.class);

            argumentAt.done(null, new Exception());
            return null;
        }).when(destination).ping(Mockito.any());

        manager.createSmartQueue(name, destination, waiting, needPriority);

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
}
