package packeteer.netty;

import java.util.logging.Level;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import packeteer.plugin.PacketeerPlugin;

public abstract class PacketListener {

    Connection connection;
    ChannelHandlerContext ctx;

    public abstract void onPacketReceive(Packet packet);

    public boolean reply(Packet packet) {
        if (connection == null || ctx == null) {
            return false;
        }

        ChannelFuture channelFuture = ctx.writeAndFlush(packet);
        channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess() && !connection.isClosing()) {
                    PacketeerPlugin.getInstance().getLogger().log(Level.SEVERE, "An unexpected error occured while sending packet", channelFuture.cause());
                }
            }
        });

        return true;
    }
}
