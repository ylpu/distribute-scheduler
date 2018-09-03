package com.yl.distribute.scheduler.client;

import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.client.callback.TaskCallback;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import com.yl.distribute.scheduler.core.resource.rpc.ResourceProxy;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class TaskClient {
	
    private static Log LOG = LogFactory.getLog(TaskClient.class);
    
    private static final TaskClient jobClient = new TaskClient();    

    
    private TaskClient() {
        new ServerDisconnectedListener().init();
    }
    
    public static TaskClient getInstance() {
        return jobClient;
    }
    
    public void submit(TaskRequest task){        
        try {
            Response response = addTask(task);
            if(response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new RuntimeException("failed to add task");
            }
            
            ResourceService service = ResourceProxy.get(ResourceService.class);
            String idleServer = service.getIdleServer(task.getJob(),task.getLastFailedServer());    
            if(StringUtils.isBlank(idleServer)) {
                throw new RuntimeException("can not get idle server to submit task");
            }
            task.setRunningServer(idleServer);  
            
            SimpleChannelPool channelPool = SchedulerClientPool.getInstance().getChannelPool(task.getJob().getPoolPath(),idleServer);
            Future<Channel> f = null;
            f = channelPool.acquire(); 
            
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    TaskCallback callback = new TaskCallback(task);                    
                    CallBackUtils.putCallback(task.getId(), callback);
                    Channel ch = f1.getNow();                    
                    ch.writeAndFlush(task).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(future.isSuccess()) {
                                LOG.info("提交任务" + task.getTaskId() + "-" + task.getId() + "到" + idleServer);                                
                                service.subResource(idleServer, task.getJob());  
                                service.increaseTask(idleServer);
                            } else {  
                            	LOG.error("提交任务" + task.getTaskId() + "-" + task.getId() + "到" + idleServer + "失败");  
                            }                                
                        }  
                    });                  
                    channelPool.release(ch);
                }                 
            });
            
        }catch(Exception e) {
        	LOG.error(e);
        	throw new RuntimeException(e);
        }
    }     
    
    private Response addTask(TaskRequest task) {
        Properties prop = Configuration.getConfig("config.properties");        
        String taskApi = Configuration.getString(prop, "task.web.api");
        return JerseyClient.add(taskApi + "/" + "addTask", task);
    }
}