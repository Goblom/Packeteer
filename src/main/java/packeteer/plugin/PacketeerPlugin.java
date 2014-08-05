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

/**
 *
 * @author Goblom
 */
public class PacketeerPlugin extends JavaPlugin {
    @Getter @Setter(lombok.AccessLevel.PROTECTED) private static PacketeerPlugin instance;
    
    @Override
    public void onLoad() {
        setInstance(this);
        startTouching(); //LOLOLOL
    }
    
    @Override
    public void onEnable() {
//        Packeteer.registerListener(new BasicPacketListener(this));
        
        getServer().getPluginManager().registerEvents(new Listener() {
            final Plugin plugin = JavaPlugin.getPlugin(PacketeerPlugin.class);
            
            @EventHandler
            public void onPlayerJoin(final PlayerJoinEvent event) {
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    public void run() {
                        Packeteer.getPlayer(event.getPlayer()).hook();
                    }
                }, 5);
                
            }
            
            @EventHandler
            public void onPlayerQuit(final PlayerQuitEvent event) {
                Packeteer.getPlayer(event.getPlayer()).unhook(); 
            }
        }, this);
    }
    
    @Override
    public void onDisable() {
        PacketListener listener = Packeteer.getInstance(BasicPacketListener.class);
        Packeteer.unregisterListener(listener);
    }
    
    public ClassLoader getPluginClassLoader() {
        return getClassLoader();
    }
    
    private void startTouching() {
        touch(Reflection.class);
        touch(Packeteer.class);
    }
    
    private void touch(Class<?> clazz) {} //do nothing
}
