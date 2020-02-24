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

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import packeteer.packet.helper.MethodInvoker;
import packeteer.packet.helper.SafeField;
import packeteer.utils.Reflection;
import packeteer.utils.Reflection.ClassType;

/**
 *
 * @author Goblom
 */
public class PacketPlayer {
    private static final String CHANNEL_NAME = "packeteer";
    
    @Getter(lombok.AccessLevel.PROTECTED)
    private final Object playerConnection;
    
    @Getter(lombok.AccessLevel.PROTECTED)
    private final Object networkManager;
    
    @Getter(lombok.AccessLevel.PROTECTED)
    private final UUID UUID;
    
    @Getter
    private final Channel channel;
    
    @Getter(lombok.AccessLevel.PROTECTED)
    private final ChannelListener channelListener;
    
    @Getter
    @Setter(lombok.AccessLevel.PRIVATE)
    private boolean hooked;
    
    PacketPlayer(Player player) {
        this.playerConnection = Reflection.getPlayerConnection(player);
        this.networkManager = new SafeField(playerConnection, "networkManager").read();
        this.channel = new SafeField<Channel>(networkManager, "channel").read();
        this.channelListener = new ChannelListener(this);
        this.UUID = player.getUniqueId();
    }

    public Player getBukkit() {
        return Bukkit.getPlayer(UUID);
    }
    
    public void hook() {
        if (isHooked() || getBukkit() == null) return;
        getChannel().pipeline().addBefore("packet_handler", CHANNEL_NAME, channelListener);
        setHooked(true);
    }

    public void unhook() {
        if (!isHooked() || getBukkit() == null) return;
        getChannel().eventLoop().submit(() -> {
            getChannel().pipeline().remove(CHANNEL_NAME);
        });
        setHooked(false);
    }
    
    public void sendPacket(Packet packet) {
        new MethodInvoker(playerConnection, "sendPacket", Reflection.getClass(ClassType.NMS, "Packet")).invoke(packet.getHandle());
    }
    
    public Object getHandle() {
        return Reflection.getHandle(getBukkit());
    }
}
