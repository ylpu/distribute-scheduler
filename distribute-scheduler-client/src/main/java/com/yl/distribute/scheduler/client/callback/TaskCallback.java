package com.yl.distribute.scheduler.client.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.client.TaskClient;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class TaskCallback{
	
    private static Log LOG = LogFactory.getLog(TaskCallback.class);
    
    private TaskRequest task;
    
    public TaskCallback(TaskRequest task) {
        this.task = task;
    }
    
    public void onRead(TaskResponse response){
    	LOG.info(task.getTaskId() + "-" + task.getId() + "返回状态是" + response.getTaskId() +  response.getTaskStatus());
    	//根据response中任务的状态来判断dag是否往下执行
    	TaskResponseManager.add(task.getJob().getJobId(),response);        
        resubmitIfNeccesery(response);
    }
    
    private void resubmitIfNeccesery(TaskResponse response){
        if(response.getTaskStatus() == TaskStatus.FAILED
                && task.getFailedTimes() < task.getJob().getRetryTimes()) {   
            System.out.println("retry " + task.getFailedTimes() + " for " + task.getJob().getJobId());
            TaskRequest newTask = new TaskRequest();
            TaskClient.getInstance().initNewTask(newTask,task);            
            TaskClient.getInstance().submit(newTask);            
        }
    }
}