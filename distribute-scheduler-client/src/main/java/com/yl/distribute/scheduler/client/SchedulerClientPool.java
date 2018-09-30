package com.yl.distribute.scheduler.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.math.NumberUtils;

import com.yl.distribute.scheduler.client.handler.SchedulerClientHander;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.zk.ZKHelper;


public class SchedulerClientPool {   
    
    //key is servername,value is channel pool
    public Map<String,SimpleChannelPool> channelPoolMap = new ConcurrentHashMap<String,SimpleChannelPool>();
    
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
    
    public synchronized SimpleChannelPool getChannelPool(String poolPath,String idleHost) {
        SimpleChannelPool channelPool = channelPoolMap.get(idleHost);
        if(channelPool == null) {
            
            Properties prop = Configuration.getConfig("config.properties");        
            int poolNumber = Configuration.getInt(prop, "channel.pool.numbers");
            String zkServers = Configuration.getString(prop, "zk.server.list");  
            ZkClient zkclient = ZKHelper.getClient(zkServers);
            HostInfo hostInfo = null;
            try {                
                 hostInfo = ZKHelper.getData(zkclient, poolPath + "/" + idleHost);
            }finally {
                if(zkclient != null) {
                   zkclient.close();
                }
            }
            if(hostInfo != null) {
                String hostName = hostInfo.getHostName().split(":")[0];
                int port = NumberUtils.toInt(hostInfo.getHostName().split(":")[1]);
                
                SchedulerClientPool clientPool = SchedulerClientPool.getInstance();            
                clientPool.build(poolNumber);
                SimpleChannelPool pool = clientPool.poolMap.get(new InetSocketAddress(hostName,port));                
                channelPoolMap.put(idleHost, pool);
                channelPool = pool; 
            }
        }
        return channelPool;
      }

    public Map<String, SimpleChannelPool> getChannelPoolMap() {
        return channelPoolMap;
    }
    
    private class SchedulerChannelPoolHandler implements ChannelPoolHandler {
    	
        public void channelReleased(Channel ch) throws Exception {
        }


        public void channelAcquired(Channel ch) throws Exception {
        }

        public void channelCreated(Channel ch) throws Exception {
            SocketChannel channel = (SocketChannel) ch;
            channel.config().setKeepAlive(true);
            channel.config().setTcpNoDelay(true);
            channel.pipeline()
                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                    .addLast(new LengthFieldPrepender(4))
                    //使用netty自己的encoder和decoder,根据需要可以使用core中的kryo或protobuf
                    .addLast(new ObjectEncoder())
                    .addLast(new ObjectDecoder(Integer.MAX_VALUE,ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))                
                    .addLast(new SchedulerClientHander());

        }
    }
 }