package com.yl.distribute.scheduler.client;

import java.util.Date;

import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.client.callback.TaskCallback;
import com.yl.distribute.scheduler.client.callback.TaskResponseManager;
import com.yl.distribute.scheduler.client.schedule.ObjectId;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import com.yl.distribute.scheduler.core.resource.rpc.ResourceProxy;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;
import com.yl.distribute.scheduler.core.task.TaskManager;
import com.yl.distribute.scheduler.core.zk.ZKResourceManager;
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
        new PoolChangeListener().init();
    }
    
    public static TaskClient getInstance() {
        return jobClient;
    }
    
    public void submit(TaskRequest task){        
        try {
            Response response = TaskManager.getInstance().addTask(task);
            if(response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new RuntimeException("failed to add task");
            }
            
            ResourceService service = ResourceProxy.get(ResourceService.class);
            String idleHost = service.getIdleHost(task.getJob(),task.getLastFailedHost());    
            if(StringUtils.isBlank(idleHost)) {
                throw new RuntimeException("can not get idle server to submit task");
            }
            task.setRunningHost(idleHost);  
            
            SimpleChannelPool channelPool = SchedulerClientPool.getInstance().getChannelPool(task.getJob().getPoolPath(),idleHost);
            if(channelPool == null) {
            	throw new RuntimeException("can not get channel from server " + idleHost);
            }
            Future<Channel> f = null;
            f = channelPool.acquire(); 
            
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    TaskCallback callback = new TaskCallback(task);                    
                    CallBackUtils.putCallback(task.getTaskId(), callback);
                    Channel ch = f1.getNow();                    
                    ch.writeAndFlush(task).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(future.isSuccess()) {
                                LOG.info("提交任务" + task.getTaskId() + "-" + task.getId() + "到" + idleHost);
                                ZKResourceManager.subZkResource(task);
                                service.subResource(idleHost, task.getJob());  
                            } else {  
                            	LOG.error("提交任务" + task.getTaskId() + "-" + task.getId() + "到" + idleHost + "失败");  
                            }                                
                        }  
                    });                  
                    channelPool.release(ch);
                }                 
            });
            
        }catch(Exception e) {
        	LOG.error(e);
        	updateTaskStatus(task,TaskStatus.FAILED);
        	resubmit(task);
        }
    } 
    
    private void updateTaskStatus(TaskRequest task,TaskStatus taskStatus){        
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTaskId(task.getTaskId());   
        response.setFailedTimes(task.getFailedTimes());
        response.setJobId(task.getJob().getJobId());
        response.setTaskStatus(taskStatus);
        TaskResponseManager.add(task.getJob().getJobId(),response);   
        TaskManager.getInstance().updateTask(task, taskStatus);
    }
    
    private void resubmit(TaskRequest task){
        if(task.getFailedTimes() < task.getJob().getRetryTimes()) {   
            System.out.println("retry " + task.getFailedTimes() + " for " + task.getJob().getJobId());
            TaskRequest newTask = new TaskRequest();
            initNewTask(newTask,task);            
            TaskClient.getInstance().submit(newTask);            
        }
    }
    
    public void initNewTask(TaskRequest newTask,TaskRequest task) {
        newTask.setTaskId(new ObjectId().toHexString());
        newTask.setJob(task.getJob());
        newTask.setStartTime(new Date());
        newTask.setEndTime(null);
        newTask.setLastFailedHost(task.getRunningHost());
        newTask.setRunningHost("");
        newTask.setFailedTimes(task.getFailedTimes() + 1);
        newTask.setStdOutputUrl("");
        newTask.setErrorOutputUrl("");
        newTask.setTaskStatus(TaskStatus.SUBMIT);
    }
}