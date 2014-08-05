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

import lombok.Getter;
import packeteer.packet.Packet;
import packeteer.utils.Utils;

/**
 *
 * @author Goblom
 */
public class FieldModifier<T> {

    private final Packet packet;
    @Getter private final String fieldName;
    @Getter private final Class<T> type;
    
    public FieldModifier(Packet packet, String field, Class<T> type) {
        this.packet = packet;
        this.type = type;
        
        if (Utils.isNumberical(field)) {
            try {
                this.fieldName = packet.getHandle().getClass().getFields()[Integer.valueOf(field)].getName();
            } catch (Exception e) {
                throw new RuntimeException("Error loading field at " + field + " for Packet " + packet.getHandle().getClass().getSimpleName());
            }
        } else {
            this.fieldName = field;
        }
    }

    public FieldModifier<T> write(T object) {
        packet.write(fieldName, object);
        return this;
    }
    
    public T read() {
        return packet.read(fieldName, type);
    }
    
    public Packet back() {
        return packet;
    }
}
