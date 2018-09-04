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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.math.NumberUtils;
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

            HostInfo hostInfo = ZKHelper.getClient(zkServers).readData(poolPath + "/" + idleHost);
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
 }