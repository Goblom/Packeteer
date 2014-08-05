package packeteer.netty;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import packeteer.plugin.PacketeerPlugin;

public class ClientConnection extends Connection {

    private final Bootstrap bootstrap;
    private final EventLoopGroup group = new NioEventLoopGroup();

    public ClientConnection(String address, int port) {
        super(address, port);

        this.bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new NettyInitializer(this, PacketeerPlugin.getInstance().getPluginClassLoader()))
                .remoteAddress(getSocketAddress());
    }

    public void connect() {
        connect(false);
    }

    public void connect(final boolean retry) {
        this.closing = false;
        this.packetHandler = new PacketHandler(this);
        bootstrap.connect().addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    if (channelFuture.cause() instanceof ConnectException && retry) {
                        channelFuture.channel().eventLoop().schedule(new Runnable() {

                            @Override
                            public void run() {
                                connect(retry);
                            }
                        }, 1, TimeUnit.SECONDS);
                    } else if (!(channelFuture.cause() instanceof ConnectException)) {
                        PacketeerPlugin.getInstance().getLogger().log(Level.WARNING, "An unexpected error occured while connecting", channelFuture.cause());
                    }
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        super.close();
        group.shutdownGracefully().syncUninterruptibly();
    }
}
