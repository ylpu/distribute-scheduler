package com.yl.distribute.scheduler.client.job;

import java.util.Date;
import java.util.UUID;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.yl.distribute.scheduler.client.TaskClient;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class UserJob implements Job{

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobConf data = (JobConf) jobExecutionContext.getJobDetail().getJobDataMap().get("data");
        TaskClient client = TaskClient.getInstance();
        TaskRequest task = new TaskRequest();
        String taskId = String.valueOf(Math.random());
        task.setJob(data);
        task.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        task.setTaskId(taskId);
        task.setStartTime(new Date()); 
        task.setTaskStatus(TaskStatus.SUBMIT);
        client.submit(task);        
    }
}
