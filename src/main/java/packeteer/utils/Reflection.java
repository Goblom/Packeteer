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
package packeteer.utils;

import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public class Reflection {

    @Getter
    private static Map<String, Class<?>> storedClasses = Maps.newHashMap();
    @Getter
    private static Map<Class<?>, Map<String, Method>> storedMethods = Maps.newHashMap();
    @Getter
    private static Map<Class<?>, Map<String, Field>> storedFields = Maps.newHashMap();

    private static String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    public static Class<?> getClass(ClassType type, String name) {
        String classPath = type.getPath() + "." + VERSION + "." + name;
        if (storedClasses.containsKey(classPath)) {
            return storedClasses.get(classPath);
        }

        try {
            Class found = Class.forName(classPath);
            storedClasses.put(classPath, found);
            return found;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Class<?> forceClass(Class clazz) {
        String classPath = clazz.getPackage().getName() + "." + clazz.getSimpleName();
        if (!storedClasses.containsKey(classPath)) {
            return storedClasses.put(classPath, clazz);
        }
        
        return clazz;
    }
    
    public static Class<?> getPacketClass(String name) {
        if (name.startsWith("PacketPlay")) {
            return getClass(ClassType.NMS, name);
        }

        return getClass(ClassType.NMS, "PacketPlay" + name);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        if (storedMethods.containsKey(clazz)) {
            Map<String, Method> map = storedMethods.get(clazz);
            if (map.containsKey(methodName)) {
                return map.get(methodName);
            }
        } else {
            storedMethods.put(clazz, Maps.<String, Method>newHashMap());
        }

        Method method = null;

        try {
            method = clazz.getMethod(methodName, params);
        } catch (Exception e) {
        }

        if (method == null) {
            try {
                method = clazz.getDeclaredMethod(methodName, params);
            } catch (Exception e) {
            }
        }

        if (method != null) {
            method.setAccessible(true);

            Map<String, Method> map = storedMethods.get(clazz);
            map.put(methodName, method);
            storedMethods.put(clazz, map);
        }

        return method;
    }

    public static Method getMethod(Object object, String name, Class<?>... params) {
        return getMethod(object.getClass(), name, params);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        if (storedFields.containsKey(clazz)) {
            Map<String, Field> map = storedFields.get(clazz);
            if (map.containsKey(fieldName)) {
                return map.get(fieldName);
            }
        } else {
            storedFields.put(clazz, Maps.<String, Field>newHashMap());
        }

        Field field = null;

        try {
            field = clazz.getField(fieldName);
        } catch (Exception e) {
        }

        if (field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
            }
        }

        if (field != null) {
            field.setAccessible(true);

            Map<String, Field> map = storedFields.get(clazz);
            map.put(fieldName, field);
            storedFields.put(clazz, map);
        }

        return field;
    }

    public static Field getField(Object obj, String name) {
        return getField(obj.getClass(), name);
    }

    public static Object invokeField(Object obj,String name) {
        Field f = getField(obj.getClass(), name);
        
        try {
            return f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @AllArgsConstructor
    public enum ClassType {

        NMS("net.minecraft.server"),
        OBC("org.bukkit.craftbukkit");

        @Getter
        private final String path;
        
        public Class<?> getClass(String name) {
            return Reflection.getClass(this, name);
        }
    }

    public static Object getHandle(Object obj) {
        try {
            return getMethod(obj, "getHandle").invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getPlayerConnection(Player player) {
        try {
            Object handle = getHandle(player);
            return handle.getClass().getField("playerConnection").get(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static void printMethods(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            System.out.println("Found Method: " + method.getName() + " with type " + method.getReturnType());
        }
        
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println("Found Declared Method: " + method.getName() + " with type " + method.getReturnType());
        }
    }
    
    public static void printFields(Class<?> clazz) {
        for (Field field : clazz.getFields()) {
            System.out.println("Found Field: " + field.getName() + " with type " + field.getType());
        }
        
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("Found Declared Field: " + field.getName() + " with type " + field.getType());
        }
    }
}
