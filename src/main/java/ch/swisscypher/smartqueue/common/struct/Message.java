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

package ch.swisscypher.smartqueue.common.struct;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private long id;
    private String method;
    private Serializable[] params;
    private Serializable returnedValue;

    public Message(long id, String method, Serializable... params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public void setReturnedValue(Serializable returnedValue) {
        this.returnedValue = returnedValue;
    }

    public long getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getReturnedValue() {
        return (T)returnedValue;
    }

    public Serializable[] getParams() {
        return params;
    }

    public String getMethod() {
        return method;
    }
}
