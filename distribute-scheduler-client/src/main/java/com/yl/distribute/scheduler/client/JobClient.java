package com.yl.distribute.scheduler.client;

import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yl.distribute.scheduler.client.callback.ClientCallback;
import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.Task;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import com.yl.distribute.scheduler.core.service.ResourceService;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class JobClient {
	
    private static Log LOG = LogFactory.getLog(JobClient.class);
    
    private static final JobClient jobClient = new JobClient();    

    
    private JobClient() {
        new ServerDisconnectedListener().init();
    }
    
    public static JobClient getInstance() {
        return jobClient;
    }
    
    public void submit(Task task){        
        try {                       
            long startTime =  System.currentTimeMillis();    
            ResourceService service = ResourceProxy.get(ResourceService.class);
            String idleServer = service.getIdleServer(task.getJob(),task.getLastFailedServer());  
            long endTime = System.currentTimeMillis();
            System.out.println("cost " + (endTime - startTime) + " to get idle server for " + task.getTaskId());
            
            task.setRunningServer(idleServer);            
            
            SimpleChannelPool channelPool = SchedulerClientPool.getInstance().getChannelPool(task.getJob().getPoolPath(),idleServer);
            Future<Channel> f = null;
            f = channelPool.acquire(); 
            
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    ClientCallback callback = new ClientCallback(task);                    
                    CallBackUtils.putCallback(task.getTaskId(), callback);
                    Channel ch = f1.getNow();                    
                    ch.writeAndFlush(task).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(future.isSuccess()) {
                                LOG.info("提交任务" + task.getTaskId() +"到"+ idleServer);                                
                                service.subResource(idleServer, task.getJob().getExecuteParameters());  
                                service.increaseTask(idleServer);
                            } else {  
                            	LOG.error("提交任务" + task.getTaskId() +"到"+ idleServer + "失败");  
                            }                                
                        }  
                    });                  
                    channelPool.release(ch);
                }                 
            });
            
        }catch(Exception e) {
        	System.out.println("任务 " + task.getTaskId() + "第 " + task.getFailedTimes() + "失败次并且找不到可运行的服务器");
        	LOG.error("任务 " + task.getTaskId() + "第 " + task.getFailedTimes() + "失败次并且找不到可运行的服务器",e);
        }
    } 

}
