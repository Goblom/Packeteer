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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import packeteer.utils.Reflection;

/**
 *
 * @author Goblom
 */
@AllArgsConstructor
public class MethodInvoker<T> {
    private final Object handle;
    @Getter private final String methodName;
    @Getter private final Class<?>[] parameters;
    
    // Allow singe param easily -- for compatibility
    public MethodInvoker(Object handle, String methodName, Class<?> param) {
        this(handle, methodName, new Class<?>[] { param });
    }
    
    public <T> T invoke(Object... args) {
        try {
            Method m = Reflection.getMethod(handle, methodName, parameters);
            
            if (handle == null || Modifier.isStatic(m.getModifiers())) {
                return (T) m.invoke(null, args);
            } else {
                return (T) m.invoke(handle, args);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
