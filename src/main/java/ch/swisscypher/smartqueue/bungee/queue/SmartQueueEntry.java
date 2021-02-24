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

package ch.swisscypher.smartqueue.bungee.queue;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class SmartQueueEntry<T> implements Comparable<SmartQueueEntry<T>> {

    private static final AtomicLong sequence = new AtomicLong(0); // OOB warning

    private final T entry;
    private final long priority;
    private final long sequenceNumber;
    private long position;

    public SmartQueueEntry(T entry, long priority, long position) {
        this.entry = entry;
        this.priority = priority;
        this.sequenceNumber = sequence.getAndIncrement();
        this.position = position;
    }

    public SmartQueueEntry(T entry) {
        this.entry = entry;
        this.priority = -1;
        this.sequenceNumber = -1;
        this.position = -1;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    @Override
    public int compareTo(SmartQueueEntry o) {
        int res = -Long.compare(priority, o.priority);
        if(res == 0) {
            return Long.compare(sequenceNumber, o.sequenceNumber);
        }
        return res;
    }

    public T getEntry() {
        return entry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartQueueEntry<?> that = (SmartQueueEntry<?>) o;
        return entry.equals(that.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry);
    }
}
