/*
 * Copyright (C) 2020 Bryan Larson
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Bryan Larson
 */
@AllArgsConstructor
public class SafeClass {

    @Getter private final Class unsafe;
    
    public <T> SafeField<T> getField(Object instance, String name) {
        return new SafeField(instance, name);
    }
    
    public <T> MethodInvoker<T> getMethod(String name, Class<?> params) {
        return getMethod(getUnsafe(), name, params);
    }
    
    public <T> MethodInvoker<T> getMethod(Object instance, String name, Class<?>... params) {
        return new MethodInvoker(instance, name, params);
    }
    
    public Object newInstance() {
        try {
            return getUnsafe().newInstance();
        } catch (IllegalAccessException | InstantiationException e) { 
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Object newInstance(Class<?>[] params, Object[] objs) {
        try {
            Constructor constructor = getUnsafe().getConstructor(params);
            return constructor.newInstance(objs);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) { 
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Object newInstance(Object... objs) {
        if (objs == null || objs.length == 0) return newInstance();
        
        Class<?>[] params = new Class<?>[objs.length];
        for (int i = 0; i < objs.length; i++) {
            params[i] = objs[i].getClass();
        }
        
        return newInstance(params, objs);
    }
}
