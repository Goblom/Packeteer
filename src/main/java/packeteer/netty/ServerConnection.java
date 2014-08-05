package packeteer.netty;

import java.io.IOException;
import java.util.logging.Level;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import packeteer.plugin.PacketeerPlugin;

public class ServerConnection extends Connection {

    private final ServerBootstrap bootstrap;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final int port;

    public ServerConnection(int port) {
        super("", port);
        this.port = port;

        this.bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyInitializer(this, PacketeerPlugin.getInstance().getPluginClassLoader()))
                .localAddress("", port);
    }

    public void bind() {
        this.closing = false;
        this.packetHandler = new PacketHandler(this);
        bootstrap.bind().addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                PacketeerPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to bind port: " + port);
            }
        });
    }

    @Override
    public void close() throws IOException {
        super.close();
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }
}
