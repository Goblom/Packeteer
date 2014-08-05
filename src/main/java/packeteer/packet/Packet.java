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

import java.lang.reflect.Field;
import lombok.Getter;
import packeteer.utils.Reflection;

/**
 *
 * @author Goblom
 */
public class Packet {
    @Getter private Class<?> provider;
    @Getter private Object handle;
    
    Packet(String name) {
        this(name, true);
    }
    
    Packet(String name, boolean instance) {
        this.provider = Reflection.getPacketClass(name);
        
        if (instance) {
            try {
                this.handle = provider.newInstance();
            } catch (Exception e) {
                System.out.println("Handle is null for " + name);
    //            e.printStackTrace();
            }
        }
    }
    
    Packet(Object obj) {
        this(obj.getClass().getSimpleName(), false);
        this.handle = obj;
        
//        System.out.println("Handle Class: " + handle.getClass());
    }
    
    public void write(String field, Object value) {
        try {
            Field f = Reflection.getField(provider, field);
            f.set(handle, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Object read(String field) {
        try {
            return Reflection.getField(provider, field).get(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public <T> T read(String field, Class<T> type) {
        return (T) read(field);
    }
}
