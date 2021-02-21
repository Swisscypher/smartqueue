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

package ch.swisscypher.smartqueue.bungee.test;

import ch.swisscypher.smartqueue.bungee.queue.SmartQueueEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class TestSmartQueueEntry {
    @Test
    public void testGet() {
        long value = new Random().nextLong();
        Assertions.assertEquals(value, new SmartQueueEntry<>(value, 0, 1).getEntry().longValue());
    }

    @Test
    public void testCompareTo() {
        SmartQueueEntry<Integer> e1 = new SmartQueueEntry<>(1, 1, 1);
        SmartQueueEntry<Integer> e2 = new SmartQueueEntry<>(1, 1, 2);
        SmartQueueEntry<Integer> e3 = new SmartQueueEntry<>(1, 1, 3);

        Assertions.assertTrue(e1.compareTo(e2) < 0);
        Assertions.assertTrue(Integer.signum(e1.compareTo(e2)) == -Integer.signum(e2.compareTo(e1)));
        Assertions.assertTrue(e3.compareTo(e2) > 0 && e2.compareTo(e1) > 0 && e3.compareTo(e1) > 0);
    }

    @Test
    public void testCompareToPriority() {
        SmartQueueEntry<Integer> e1 = new SmartQueueEntry<>(1, 1, 1);
        SmartQueueEntry<Integer> e2 = new SmartQueueEntry<>(1, 1, 2);
        SmartQueueEntry<Integer> e3 = new SmartQueueEntry<>(1, 2, 3);

        Assertions.assertTrue(e1.compareTo(e2) < 0);
        Assertions.assertTrue(e3.compareTo(e2) < 0);
        Assertions.assertTrue(e3.compareTo(e1) < 0);
    }
}
