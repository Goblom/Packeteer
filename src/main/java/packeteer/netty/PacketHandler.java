package packeteer.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import packeteer.plugin.PacketeerPlugin;

@Sharable
public class PacketHandler extends ChannelInboundHandlerAdapter implements Closeable {

    private final Connection connection;
    private final List<ChannelHandlerContext> channelHandlers = new ArrayList<ChannelHandlerContext>();

    public PacketHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelHandlers.add(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (channelHandlers.remove(ctx) && !connection.isClosing()) {
            if (connection instanceof ClientConnection) {
                PacketeerPlugin.getInstance().getLogger().log(Level.SEVERE, "Unexpectedly lost connection, trying to reconnect...");
                ctx.channel().eventLoop().schedule(new Runnable() {

                    @Override
                    public void run() {
                        ((ClientConnection) connection).connect();
                    }
                }, 5, TimeUnit.SECONDS);
            } else {
                PacketeerPlugin.getInstance().getLogger().log(Level.SEVERE, "Unexpectedly lost connection, listening for reconnection.");
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = (Packet) msg;

        if (packet instanceof DisconnectPacket) {
            channelHandlers.remove(ctx);
        } else {
            connection.onPacketReceive(ctx, packet);
        }
    }

    public void send(Packet packet) {
        for (ChannelHandlerContext ctx : channelHandlers) {
            ChannelFuture channelFuture = ctx.writeAndFlush(packet);
            channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {

                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess() && !connection.isClosing()) {
                        PacketeerPlugin.getInstance().getLogger().log(Level.SEVERE, "An unexpected error occured while sending packet", channelFuture.cause());
                    }
                }
            });
        }
    }

    public int getHandlerCount() {
        return channelHandlers.size();
    }

    @Override
    public void close() throws IOException {
        for (ChannelHandlerContext ctx : channelHandlers) {
            ctx.channel().writeAndFlush(new DisconnectPacket()).syncUninterruptibly();
            ctx.channel().disconnect().syncUninterruptibly();
        }

        channelHandlers.clear();
    }
}
