package com.yl.distribute.scheduler.client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;

import com.yl.distribute.scheduler.client.callback.ClientCallback;
import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.client.resource.ResourceManager;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import com.yl.distribute.scheduler.core.service.ResourceService;
import com.yl.distribute.scheduler.core.zk.ZKHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class JobClient {
    
    private static JobClient jobClient = new JobClient();    
    
    //key is servername,value is channel pool
    public Map<String,SimpleChannelPool> channelPoolMap = new HashMap<String,SimpleChannelPool>();
    
    private JobClient() {
        
    }
    
    public static JobClient getInstance() {
        return jobClient;
    }
    
    public void submit(JobRequest input){        
        try {
            String lastFailedServer = getLastFailedJob(input.getRequestId());
            ResourceService service = ResourceProxy.get(ResourceService.class);
            String idleServer = service.getIdleServer(input,lastFailedServer);    
            SimpleChannelPool channelPool = getChannelPool(input.getPoolName(),idleServer);
            Future<Channel> f = null;
            f = channelPool.acquire(); 
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    ClientCallback callback = new ClientCallback(input);                    
                    CallBackUtils.putCallback(input.getRequestId(), callback);
                    Channel ch = f1.getNow();                    
                    ch.writeAndFlush(input).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(future.isSuccess()) {
                                service.subResource(idleServer, input.getExecuteParameters());  
                            } else {  
                                System.out.println("写数据" + input +"到"+ idleServer + "失败");  
                            }  
                                
                        }  
                    });                  
                    channelPool.release(ch);
                }                 
            });
            
        }catch(Exception e) {
            System.out.println("任务 " + input.getRequestId() + " 失败第 " + input.getRetryTimes() + "次并且找不到可运行的服务器");
        }
    } 
    
    private String getLastFailedJob(String jobId) {
        Properties prop = Configuration.getConfig("config.properties");        
        String jobApi = Configuration.getString(prop, "job.web.api");
        return JerseyClient.get(jobApi + "/" + "getLastFailedServer" + "/" + jobId,String.class);
    }
    
    private synchronized SimpleChannelPool getChannelPool(String poolName,String serverName) {
       SimpleChannelPool channelPool = channelPoolMap.get(serverName);
       if(channelPool == null) {
           Properties prop = Configuration.getConfig("config.properties");        
           int poolNumber = Configuration.getInt(prop, "channel.pool.numbers");
           String zkServers = Configuration.getString(prop, "zk.server.list");
           SchedulerClientPool clientPool = SchedulerClientPool.getInstance();
           HostInfo serverData = ZKHelper.getClient(zkServers).readData("/root" + "/" +poolName + "/" + serverName);
           clientPool.build(poolNumber);
           SimpleChannelPool pool = clientPool.poolMap.get(new InetSocketAddress(serverData.getIpAddress().split(":")[0],
                         Integer.parseInt(serverData.getIpAddress().split(":")[1])));                
           channelPoolMap.put(serverName, pool);
           channelPool = channelPoolMap.get(serverName);
       }
       return channelPool;
     }    
}
