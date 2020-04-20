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

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import packeteer.packet.PacketListener;
import packeteer.packet.Packeteer;

/**
 *
 * @author Goblom
 */
class PacketeerListener implements Listener {

    private final Plugin plugin;
    
    public PacketeerListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(plugin, Packeteer.getPlayer(event.getPlayer())::hook);

    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
//        Packeteer.getPlayer(event.getPlayer()).unhook();
    }
    
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        List<PacketListener> listeners = Packeteer.getPacketListeners();
        Plugin disabled = event.getPlugin();
        
        listeners.stream().filter((l) -> {
            try {
                return JavaPlugin.getProvidingPlugin(l.getClass()).equals(disabled);
            } catch (Exception e) {
                return false;
            }
        }).forEach(Packeteer::unregisterListener);
    }
}
