package rocket.external.socketio.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocket.external.socketio.config.Configuration;
import rocket.external.socketio.config.SocketConfig;
import rocket.external.socketio.pipeline.SocketIOChannelInitializer;

import java.net.InetSocketAddress;

/**
 * Created by hanwm on 17/3/24.
 */
public class SocketIOServer {

    private Logger logger = LoggerFactory.getLogger(SocketIOServer.class);

    private SocketIOChannelInitializer channelInitializer = new SocketIOChannelInitializer();
    private Configuration configuration;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public SocketIOServer(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start(){
        bossGroup = new NioEventLoopGroup(configuration.getBossThreads());
        workerGroup = new NioEventLoopGroup(configuration.getWorkerThreads());
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        initServer(serverBootstrap);
        logger.info("SocketIO server started at port: {}", configuration.getPort());
    }

    public void stop() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    private void initServer(ServerBootstrap serverBootstrap){
        SocketConfig config = configuration.getSocketConfig();

        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer)
                .childOption(ChannelOption.TCP_NODELAY, config.isTcpNoDelay())
                .option(ChannelOption.SO_REUSEADDR, config.isReuseAddress());
        if (config.getSoLinger() != -1) {
            serverBootstrap.option(ChannelOption.SO_LINGER, config.getSoLinger());
        }

        InetSocketAddress addr = new InetSocketAddress(configuration.getPort());
        if (configuration.getHostname() != null) {
            addr = new InetSocketAddress(configuration.getHostname(), configuration.getPort());
        }
        serverBootstrap.bind(addr).syncUninterruptibly();
    }

}
