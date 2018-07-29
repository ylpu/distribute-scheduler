package com.yl.distribute.scheduler.client.callback;

import java.util.Date;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yl.distribute.scheduler.client.JobClient;
import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.bean.Task;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import com.yl.distribute.scheduler.core.service.ResourceService;

public class ClientCallback{
	
    private static Log LOG = LogFactory.getLog(ClientCallback.class);
    
    private Task task;
    
    public ClientCallback(Task task) {
        this.task = task;
    }
    
    public void onRead(TaskResponse response) throws Exception {
    	System.out.println(task.getTaskId() + "返回状态是" + response.getTaskStatus());
    	LOG.info(task.getTaskId() + "返回状态是" + response.getStdOutputUrl());
        
        ResourceService service = ResourceProxy.get(ResourceService.class);
        service.addResource(task.getRunningServer(), task.getJob().getExecuteParameters());
        service.decreaseTask(task.getRunningServer());
        
    	Task responseTask = new Task();
    	responseTask.setTaskId(response.getTaskId());
    	responseTask.setEndTime(new Date());
    	responseTask.setTaskStatus(response.getTaskStatus());
        updateTask(responseTask);

        resubmitIfNeccesery(responseTask);
    }
    
    private Response updateTask(Task task) {
        Properties prop = Configuration.getConfig("config.properties");        
        String jobApi = Configuration.getString(prop, "task.web.api");
        return JerseyClient.update(jobApi + "/" + "updateTask", task);
    }
    
    private void resubmitIfNeccesery(Task responseTask) throws Exception {
        if(responseTask.getTaskStatus().equals(TaskStatus.FAILED.getStatus())
                && task.getFailedTimes() < task.getJob().getRetryTimes()) {        	
        	task.setStartTime(new Date());
        	task.setEndTime(new Date());
        	task.setLastFailedServer(task.getRunningServer());
        	task.setRunningServer("");
            task.setFailedTimes(task.getFailedTimes() + 1);
            task.setStdOutputUrl("");
            task.setErrorOutputUrl("");
            task.setTaskStatus(TaskStatus.INITIAL.getStatus());
            
            LOG.warn("任务"+ task.getTaskId() + "执行失败，现在进行第" + task.getFailedTimes() + "次重试");
            JobClient.getInstance().submit(task);            
        }
    }
}