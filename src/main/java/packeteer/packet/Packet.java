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

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.List;
import lombok.Getter;
import packeteer.packet.helper.FieldModifier;
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
            Field f = Reflection.getField(handle, field);
            f.set(handle, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Object read(String field) {
        try {
            return Reflection.getField(handle, field).get(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public <T> T read(String field, Class<T> type) {
        return (T) read(field);
    }
    
    public void write(int index, Object value) {
        try {
            Field f = handle.getClass().getFields()[index];
            f.set(handle, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Object read(int index) {
        try {
            return handle.getClass().getFields()[index].get(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public <T> T read(int index, Class<T> type) {
        return (T) read(index);
    }
    
    public FieldModifier<Object> modify(int field) {
        return new FieldModifier<Object>(this, String.valueOf(field), Object.class);
    }
    
    public FieldModifier<Object> modify(String field) {
        return new FieldModifier<Object>(this, field, Object.class);
    }
    
    public <T> FieldModifier<T> modify(int field, Class<T> type) {
        return new FieldModifier<T>(this, String.valueOf(field), type);
    }
    
    public <T> FieldModifier<T> modify(String field, Class<T> type) {
        return new FieldModifier<T>(this, field, type);
    }
    
    public <T> List<FieldModifier<T>> modify(Class<T> type) {
        List<FieldModifier<T>> list = Lists.newArrayList();
        for (Field field : getHandle().getClass().getFields()) {
            if (field.getType().equals(type)) {
                list.add(modify(field.getName(), type));
            }
        }
        return list;
    }
}
