package packeteer.netty;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public class Connection {

    @Getter
    private final InetSocketAddress socketAddress;
    private final List<PacketListener> packetListeners = Collections.synchronizedList(new ArrayList<PacketListener>());
    protected PacketHandler packetHandler;
    @Getter
    protected boolean closing;

    public Connection(String address, int port) {
        this.socketAddress = new InetSocketAddress(address, port);
    }

    public boolean isConnected() {
        return packetHandler != null;
    }

    public void registerPacketListener(PacketListener listener) {
        packetListeners.add(listener);
    }

    public void unregisterPacketListener(PacketListener listener) {
        packetListeners.remove(listener);
    }

    public void sendPacket(Packet packet) {
        if (packetHandler != null) {
            packetHandler.send(packet);
        }
    }

    public int getConnectionCount() {
        return packetHandler != null ? packetHandler.getHandlerCount() : 0;
    }

    void onPacketReceive(ChannelHandlerContext ctx, Packet packet) {
        for (PacketListener listener : packetListeners) {
            listener.connection = this;
            listener.ctx = ctx;
            listener.onPacketReceive(packet);
            listener.connection = null;
            listener.ctx = null;
        }
    }

    public void close() throws IOException {
        if (packetHandler != null) {
            this.closing = true;
            packetHandler.close();
            this.packetHandler = null;
        }
    }
}
