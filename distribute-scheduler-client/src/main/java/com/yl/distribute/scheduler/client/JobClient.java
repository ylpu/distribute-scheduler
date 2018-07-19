package com.yl.distribute.scheduler.client;

import java.util.Properties;
import com.yl.distribute.scheduler.client.callback.ClientCallback;
import com.yl.distribute.scheduler.client.resource.ResourceManager;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.config.Configuration;
import com.yl.distribute.scheduler.common.jersey.JerseyClient;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class JobClient {
    
    private static JobClient jobClient = new JobClient();
    
    private JobClient() {
        
    }
    
    public static JobClient getInstance() {
        return jobClient;
    }
    
    public void submit(JobRequest input){
        
        ResourceManager resource = ResourceManager.getInstance();
        try {
            String lastFailedServer = getLastFailedJob(input.getRequestId());
            String idleServer = resource.getIdleServer(input,lastFailedServer);    
            SimpleChannelPool channelPool = resource.getIdleServerChannel(idleServer);
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
                                resource.subResource(idleServer, input.getExecuteParameters());  
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
}
