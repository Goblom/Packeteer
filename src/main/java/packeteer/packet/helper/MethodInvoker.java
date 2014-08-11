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
public class MethodInvoker<T> {
    private final Packet packet;
    @Getter private final String methodName;
    @Getter private final Class<?>[] parameters;
    
    public MethodInvoker(Packet packet, String method, Class<?>[] params) {
        this.packet = packet;
        this.parameters = params;
        
        if (Utils.isNumberical(method)) {
            try {
                this.methodName = packet.getHandle().getClass().getMethods()[Integer.valueOf(method)].getName();
            } catch (Exception e) {
                throw new RuntimeException("Error loading method at " + method + " for Packet " + packet.getHandle().getClass().getSimpleName());
            }
        } else {
            this.methodName = method;
        }
    }
    
    public T invoke(Object... args) {
        return (T) packet.invoke(methodName, parameters, args);
    }
    
    public Object invokeBasic(Object... args) {
        return packet.invoke(methodName, parameters, args);
    }
    
    public Packet back() {
        return packet;
    }
}
