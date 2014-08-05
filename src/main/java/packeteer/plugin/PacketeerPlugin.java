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

package packeteer.plugin;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import packeteer.packet.PacketListener;
import packeteer.packet.Packeteer;
import packeteer.utils.Reflection;
import packeteer.utils.Utils;

/**
 *
 * @author Goblom
 */
public class PacketeerPlugin extends JavaPlugin {
    @Getter @Setter(lombok.AccessLevel.PROTECTED) private static PacketeerPlugin instance;
    
    @Override
    public void onLoad() {
        setInstance(this);
    }
    
    @Override
    public void onEnable() {
        Packeteer.register(this);
//        Packeteer.registerListener(new BasicPacketListener(this));
    }
    
    @Override
    public void onDisable() {
        PacketListener listener = Packeteer.getInstance(BasicPacketListener.class);
        Packeteer.unregisterListener(listener);
    }
    
    public ClassLoader getPluginClassLoader() {
        return getClassLoader();
    }
}
