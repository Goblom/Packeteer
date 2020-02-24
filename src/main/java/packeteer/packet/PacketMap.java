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

package packeteer.packet;

import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Goblom
 */
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
class PacketMap {
    @Getter private final Method method;
    @Getter private final PacketListener listener;
    @Getter private final PacketType packetType;
    private String forPacket;
    
    public String getForPacket() {
        if (!forPacket.equalsIgnoreCase("ALL")) {
            switch (packetType) {
                case INCOMING:
                    if (forPacket.startsWith("PacketPlayIn")) {
                        return forPacket;
                    } else {
                        return forPacket = "PacketPlayIn" + forPacket;
                    }
                case OUTGOING:
                    if (forPacket.startsWith("PacketPlayOut")) {
                        return forPacket;
                    } else {
                        return forPacket = "PacketPlayOut" + forPacket;
                    }
            }
        }
        return forPacket;
    }
}
