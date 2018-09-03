package com.yl.distribute.scheduler.resource.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.yl.distribute.scheduler.resource.handler.ResourceServerHandler;
import com.yl.distribute.scheduler.resource.jmx.ResourceManagerAgent;
import com.yl.distribute.scheduler.resource.manager.ResourceManager;

public class ResourceServer {
    
    private int zkPort;
    
    public ResourceServer(int zkPort) {
        this.zkPort = zkPort;
    }
    
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()        
                            .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                            .addLast(new LengthFieldPrepender(4))
                            //使用netty自己的encoder和decoder,根据需要可以使用core中的kryo或protobuf
                            .addLast(new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                            .addLast(new ObjectEncoder())
                            .addLast(new DefaultEventExecutorGroup(8),new ResourceServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(zkPort).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }   
    
    public static void main(String[] args) throws Exception {        
        int port = 8088;
        String rootPool = "";        
        if (args.length > 1) {          
            port = NumberUtils.toInt(args[0]); 
            rootPool = args[1];     
        }        
        if(StringUtils.isEmpty(rootPool)) {
            ResourceManager.getInstance().init();
        }else {
            ResourceManager.getInstance().init(rootPool);
        }    
        //start jmx monitor
        new ResourceManagerAgent().start();
        System.out.println("start resource manager");
        ResourceServer server = new ResourceServer(port);
        server.start();         
    }
}