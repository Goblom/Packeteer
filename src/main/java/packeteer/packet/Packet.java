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
import java.lang.reflect.Method;
import java.util.List;
import lombok.Getter;
import packeteer.packet.helper.SafeField;
import packeteer.packet.helper.MethodInvoker;
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

    Packet(Class<?> provider, boolean instance) {
        this.provider = Reflection.forceClass(provider);
        
        if (instance) {
            try {
                this.handle = provider.newInstance();
            } catch (Exception e) {
                System.out.println("Handle is null for " + provider.getSimpleName());
    //            e.printStackTrace();
            }
        }
    }
    
    Packet(Object obj) {
        this(obj.getClass(), false);
        this.handle = obj;
        
//        System.out.println("Handle Class: " + handle.getClass());
    }
    
    public <T> MethodInvoker<T> getMethod(String name, Class<?>... params) {
        return new MethodInvoker<T>(handle, name, params);
    }
    
    public <T> MethodInvoker<T> getMethod(int index, Class<?>... params) {
        return new MethodInvoker<T>(handle, index, params);
    }
    
    public <T> SafeField<T> getField(String name) {
        return new SafeField<T>(handle, name);
    }
    
    public <T> SafeField<T> getField(int index) {
        return new SafeField<T>(handle, index);
    }
    
    public <T> T read(String field) {
        return (T) getField(field).read();
    }
    
    public void write(String name, Object value) {
        SafeField field = getField(name);
        field.write(value);
    }
    
    public <T> List<SafeField<T>> collectFields(Class<T> type) {
        List<SafeField<T>> list = Lists.newArrayList();
        
        for (Field field : getHandle().getClass().getFields()) {
            if (field.getType().equals(type)) {
                list.add(new SafeField<T>(this, field.getName()));
            }
        }
        
        return list;
    }
    
    public <T> List<MethodInvoker<T>> collectMethods(Class<?> returnType) {
        List<MethodInvoker<T>> list = Lists.newArrayList();
        
        for (Method method : getHandle().getClass().getMethods()) {
            if (method.getReturnType().equals(returnType)) {
                list.add(new MethodInvoker<T>(this, method.getName(), method.getParameterTypes()));
            }
        }
        
        return list;
    }
}
