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
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import packeteer.plugin.PacketeerListener;
import packeteer.utils.Reflection;
import packeteer.utils.Utils;

/**
 *
 * @author Goblom
 */
public class Packeteer {

    @Getter(lombok.AccessLevel.PROTECTED) private static Plugin plugin;
    private static List<PacketMap> packetMap = Collections.synchronizedList(Lists.<PacketMap>newArrayList());
    private static Map<UUID, PacketPlayer> handles = Maps.newHashMap();
    private static boolean timings;

    public static PacketPlayer getPlayer(Player player) {
        if (Packeteer.handles.containsKey(player.getUniqueId())) {
            return Packeteer.handles.get(player.getUniqueId());
        }

        PacketPlayer packet = new PacketPlayer(player);
        handles.put(player.getUniqueId(), packet);
        return packet;
    }

    protected static boolean handleIncoming(PacketPlayer player, Object packet) {
        long start = System.nanoTime();
        Packet p = new Packet(packet);

        PacketEvent event = new PacketEvent(p, player);
        for (PacketMap map : Packeteer.packetMap) {
            if (map.getPacketType().equals(PacketType.INCOMING) && (!map.getForPacket().isEmpty() ? event.getPacket().getHandle().getClass().getSimpleName().equalsIgnoreCase(map.getForPacket()) || map.getForPacket().equals("ALL") : true)) {
                try {
                    map.getMethod().invoke(map.getListener(), event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (timings) {
            Packeteer.plugin.getLogger().info("INCOMING " + event.getPacket().getHandle().getClass().getSimpleName() + " took " + (System.nanoTime() - start) + " nano seconds.");
        }
        return !event.isCancelled();
    }

    protected static boolean handleOutgoing(PacketPlayer player, Object packet) {
        long start = System.nanoTime();
        Packet p = new Packet(packet);

        PacketEvent event = new PacketEvent(p, player);
        for (PacketMap map : Packeteer.packetMap) {
            if (map.getPacketType().equals(PacketType.OUTGOING) && (!map.getForPacket().isEmpty() ? event.getPacket().getHandle().getClass().getSimpleName().equalsIgnoreCase(map.getForPacket()) || map.getForPacket().equals("ALL") : true)) {
                try {
                    map.getMethod().invoke(map.getListener(), event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (timings) {
            Packeteer.plugin.getLogger().info("OUTGOING " + event.getPacket().getHandle().getClass().getSimpleName() + " took " + (System.nanoTime() - start) + " nano seconds.");
        }
        return !event.isCancelled();
    }

    public static Packet createPacket(String name) throws Exception {
        return new Packet(name, true);
    }

    public static void registerListener(PacketListener listener) {
        Class<?> clazz = listener.getClass();
        while (clazz != null) {
            for (Method method : clazz.getMethods()) {
                PacketHandler handler = method.getAnnotation(PacketHandler.class);
                if (handler != null) {
                    method.setAccessible(true);
                    Packeteer.plugin.getLogger().warning("Found " + handler.type() + " PacketHandler for " + method.getName() + " in " + listener.getClass().getSimpleName());
                    packetMap.add(new PacketMap(method, listener, handler.type(), handler.packet()));
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static void unregisterListener(PacketListener listener) {
        Iterator<PacketMap> it = Packeteer.packetMap.iterator();
        while (it.hasNext()) {
            PacketMap map = it.next();

            if (map.getListener().getClass().equals(listener.getClass())) {
                it.remove();
                break;
            }
        }
    }

    public static PacketListener getInstance(Class<? extends PacketListener> clazz) {
        for (PacketMap map : Packeteer.packetMap) {
            if (map.getListener().getClass().equals(clazz)) {
                return map.getListener();
            }
        }

        return null;
    }

    public static void showTimings(boolean show) {
        Packeteer.timings = show;
    }
    
    public static List<PacketListener> getPacketListeners() {
        List<PacketListener> list = Lists.newArrayList();
        for (PacketMap map : Packeteer.packetMap) {
            list.add(map.getListener());
        }
        
        return list;
    }
    
    private static boolean registered = false;
    public static void register(Plugin plugin) {
        if (Packeteer.registered) {
            throw new UnsupportedOperationException("Packeteer is already registered!");
        }
        
        Packeteer.plugin = plugin;
        Utils.touch(Reflection.class);
        Bukkit.getPluginManager().registerEvents(new PacketeerListener(plugin), plugin);
    }
}
