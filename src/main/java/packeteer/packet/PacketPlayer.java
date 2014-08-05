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
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import packeteer.utils.Reflection;
import packeteer.utils.Reflection.ClassType;

/**
 *
 * @author Goblom
 */
public class PacketPlayer {
    private static final String CHANNEL_NAME = "packeteer";
    
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
        Object playerConnection = Reflection.getPlayerConnection(player);
        this.networkManager = Reflection.invokeField(playerConnection, "networkManager");
        this.channel = (Channel) Reflection.invokeField(networkManager, "m");
        this.channelListener = new ChannelListener(this);
        this.UUID = player.getUniqueId();
    }

    public Player getBukkit() {
        return Bukkit.getPlayer(UUID);
    }
    
    public void hook() {
        if (isHooked() || getBukkit() == null) return;
        Packeteer.getPlugin().getLogger().info("Hooking " + getBukkit().getName());
        getChannel().pipeline().addBefore("packet_handler", CHANNEL_NAME, channelListener);
        setHooked(true);
    }

    public void unhook() {
        if (!isHooked() || getBukkit() == null) return;
        Packeteer.getPlugin().getLogger().info("UnHooking " + getBukkit().getName());
        getChannel().eventLoop().submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                getChannel().pipeline().remove(CHANNEL_NAME);
                return null;
            }
        });
        setHooked(false);
    }
    
    public void sendPacket(Packet packet) {
        try {
            Reflection.getClass(ClassType.NMS, "PlayerConnection").getMethod("sendPacket", Reflection.getClass(Reflection.ClassType.NMS, "Packet")).invoke(Reflection.getPlayerConnection(getBukkit()), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Object getHandle() {
        return Reflection.getHandle(getBukkit());
    }
}
