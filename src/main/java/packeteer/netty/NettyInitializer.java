package packeteer.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyInitializer extends ChannelInitializer<SocketChannel> {

    private static final ObjectEncoder objectEncoder = new ObjectEncoder();
    private final Connection connection;
    private final ClassLoader classLoader;

    public NettyInitializer(Connection connection, ClassLoader classLoader) {
        this.connection = connection;
        this.classLoader = classLoader;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(
                objectEncoder,
                new ObjectDecoder(ClassResolvers.weakCachingResolver(classLoader)),
                connection.packetHandler,
                new IdleStateHandler(5000, 0, 0));
    }
}
