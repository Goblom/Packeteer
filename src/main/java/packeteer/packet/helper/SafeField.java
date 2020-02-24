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

import java.lang.reflect.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;
import packeteer.utils.Reflection;

/**
 *
 * @author Goblom
 */
@AllArgsConstructor
public class SafeField<T> {
    private final Object handle;
    @Getter private final String fieldName;

    public boolean exists() {
        return Reflection.getField(handle, fieldName) != null;
    }
    
    public void write(T object) {
        try {
            Field f = Reflection.getField(handle, fieldName);
            f.set(handle, object);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    public T read() {
        try {
            return (T) Reflection.getField(handle, fieldName).get(handle);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
