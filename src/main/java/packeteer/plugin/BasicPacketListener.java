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

import org.bukkit.plugin.Plugin;
import packeteer.packet.PacketEvent;
import packeteer.packet.PacketHandler;
import packeteer.packet.PacketListener;
import packeteer.packet.PacketType;

/**
 *
 * @author Goblom
 */
class BasicPacketListener implements PacketListener {

    private final Plugin plugin;
    
    BasicPacketListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @PacketHandler(type = PacketType.INCOMING, packet = "ALL")
    public void onPacketReceive(PacketEvent event) {
        plugin.getLogger().info("Recieved " + event.getPacket().getHandle().getClass().getSimpleName() + " from " + event.getPlayer().getBukkit().getName());
    }

    @PacketHandler(type = PacketType.OUTGOING, packet = "ALL")
    public void onPacketSend(PacketEvent event) {
        plugin.getLogger().info("Sent " + event.getPacket().getHandle().getClass().getSimpleName() + " to " + event.getPlayer().getBukkit().getName());
    }
}
