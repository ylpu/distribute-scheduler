package com.yl.distribute.scheduler.client.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.yl.distribute.scheduler.client.TaskClient;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

/**
 * job plan中的一个任务是定时任务
 *
 */
public class UserJob implements Job{

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        TaskRequest task = (TaskRequest) jobExecutionContext.getJobDetail().getJobDataMap().get("data");
        TaskClient client = TaskClient.getInstance();
        task.setTaskStatus(TaskStatus.SUBMIT);
        client.submit(task);      
    }
}
