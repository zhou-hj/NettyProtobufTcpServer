package cn.xiaosheng996.NettyProtobufTcpServer;



import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NettyTcpServer{
    private static final Logger log = LoggerFactory.getLogger(NettyTcpServer.class);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ServerBootstrap bootstrap;

    private int upLimit = 2048;
    private int downLimit = 5120;

    public NettyTcpServer() {
	    bossGroup = new NioEventLoopGroup();
	    workerGroup = new NioEventLoopGroup(4);
	    bootstrap = new ServerBootstrap();
	    bootstrap.group(bossGroup, workerGroup)
			    .channel(NioServerSocketChannel.class)
			    .option(ChannelOption.SO_BACKLOG, 5)
			    .childOption(ChannelOption.TCP_NODELAY, true);
    }

    public void bind(String ip, int port) {
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("decoder", new ProtoDecoder(upLimit))
                        .addLast("server-handler", new ServerHandler()) 
                        .addLast("encoder", new ProtoEncoder(downLimit));
            }
        });
        InetSocketAddress address = new InetSocketAddress(ip, port);
        try {
            bootstrap.bind(address).sync();
        } catch (InterruptedException e) {
            log.error("bind "+ip+":"+port+" failed", e);
            shutdown();
        }
    }

    public void start() {
    	log.info("Netty Tcp Server started..");
    }

    public void shutdown() {
        try {
            bossGroup.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error("shutdown boss group failed", e);
        }
        try {
            workerGroup.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error("shutdown worker group failed", e);
        }
    }
}
