package com.yl.distribute.scheduler.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;


public class SchedulerClientPool {
    
    private static SchedulerClientPool jobClient = new SchedulerClientPool();
    
    private SchedulerClientPool() {
        
    }
    
    public static SchedulerClientPool getInstance() {
        return jobClient;
    }
    
    private EventLoopGroup group = new NioEventLoopGroup();
    private  Bootstrap strap = new Bootstrap();    
    public ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap; 
    
    public void build(int connections){
        strap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(strap.remoteAddress(key), new SchedulerChannelPoolHandler(), connections);
            }
        };
    }
 }