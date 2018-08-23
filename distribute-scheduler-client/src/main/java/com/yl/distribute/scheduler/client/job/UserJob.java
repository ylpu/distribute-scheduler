package com.yl.distribute.scheduler.client.job;

import java.util.Date;
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
        task.setJob(data);        
        task.setId(new ObjectId().toHexString());
        task.setTaskId(new ObjectId().toHexString());
        task.setStartTime(new Date()); 
        task.setTaskStatus(TaskStatus.SUBMIT);
        client.submit(task);        
    }
}
