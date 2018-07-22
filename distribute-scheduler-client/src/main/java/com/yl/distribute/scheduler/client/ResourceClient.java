package com.yl.distribute.scheduler.client;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.client.handler.ResourceClientHandler;
import com.yl.distribute.scheduler.common.bean.ResourceRequest;
import com.yl.distribute.scheduler.common.bean.ResourceResponse;
import com.yl.distribute.scheduler.core.config.Configuration;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
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
	
	private static Log LOG = LogFactory.getLog(ResourceClient.class);
	
	private static AtomicInteger reconnectttimes = new AtomicInteger(0);

    private Channel channel;

    private EventLoopGroup group;

    public static ResourceClient connect() throws InterruptedException {
    	
 	    Properties prop = Configuration.getConfig("config.properties");        
 	    String resourceServer = Configuration.getString(prop, "resource.manager.server");
 	    int resourcePort = Configuration.getInt(prop, "resource.manager.port");
 	    long interval = Configuration.getLong(prop, "resource.manager.interval");
 	    int reconnectedtimes = Configuration.getInt(prop, "resource.manager.reconnectedtimes");
 	   
        ResourceClient client = new ResourceClient();        
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
                        .addLast(new ResourceClientHandler());
            }
        });

        ChannelFuture future = bootstrap.connect(resourceServer, resourcePort).addListener(new ChannelFutureListener(){
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess() && reconnectttimes.getAndIncrement() < reconnectedtimes) {  
				      System.out.println("Reconnect to " + resourceServer + ":" + resourcePort);  
				      final EventLoop loop = future.channel().eventLoop();  
				      loop.schedule(new Runnable() {  
				        public void run() {  
				        	try {
								connect();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}  
				        }  
				      }, interval, TimeUnit.SECONDS);  
				    } 			
			   }        	
        }).sync();
        Channel c = future.channel();
        client.setChannel(c);
        client.setGroup(group);
        return client;
    }

    public ResourceResponse invoke(ResourceRequest request) throws Exception {
        ResourceClientHandler handle = channel.pipeline().get(ResourceClientHandler.class);
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
 }
