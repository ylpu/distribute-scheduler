package com.yl.distribute.scheduler.client;

import java.util.*;
import com.yl.distribute.scheduler.client.handler.ResourceHandler;
import com.yl.distribute.scheduler.common.bean.ResourceRequest;
import com.yl.distribute.scheduler.common.bean.ResourceResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ResourceClient {
    //已连接主机的缓存
    private static Map<String, ResourceClient> clientMap = new HashMap<String, ResourceClient>();

    private Channel channel;

    private EventLoopGroup group;

    private String ip;

    private int port;

    private ResourceClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static ResourceClient getConnect(String host, int port) throws InterruptedException {
        if (clientMap.containsKey(host + port)) {
            return clientMap.get(host + port);
        }
        ResourceClient con = connect(host, port);
        clientMap.put(host + port, con);
        return con;
    }

    private static ResourceClient connect(String host, int port) throws InterruptedException {
        ResourceClient client = new ResourceClient(host, port);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4))
                        .addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                        .addLast(new LengthFieldPrepender(4))
                        .addLast(new ObjectEncoder())
                        .addLast(new ResourceHandler());
            }
        });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        Channel c = future.channel();

        client.setChannel(c);
        client.setGroup(group);
        return client;
    }

    public ResourceResponse invoke(ResourceRequest request) throws Exception {
        ResourceHandler handle = channel.pipeline().get(ResourceHandler.class);
        return handle.invoke(request);
    }

    public void closeConnect() {
        this.group.shutdownGracefully();
    }


    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setGroup(EventLoopGroup group) {
        this.group = group;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
