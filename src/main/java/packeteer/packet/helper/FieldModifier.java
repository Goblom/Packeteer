/*
 * Copyright (C) 2014 Goblom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package packeteer.packet.helper;

import packeteer.packet.Packet;

/**
 *
 * @author Goblom
 */
public class FieldModifier<T> {

    private final Packet packet;
    private final String field;
    private final Class<T> type;
    
    public FieldModifier(Packet packet, String field, Class<T> type) {
        this.packet = packet;
        this.field = field;
        this.type = type;
    }

    public FieldModifier<T> write(T object) {
        packet.write(field, object);
        return this;
    }
    
    public T read() {
        return packet.read(field, type);
    }
    
    public Packet back() {
        return packet;
    }
}
