package com.yl.distribute.scheduler.client.test;

import org.junit.Test;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.yl.distribute.scheduler.client.schedule.JobScheduler;
import com.yl.distribute.scheduler.client.schedule.UserJob;
import com.yl.distribute.scheduler.common.bean.JobScheduleInfo;

public class JobSchedulerTest {
    public static String JOB_NAME = "动态任务调度";  
    public static String TRIGGER_NAME = "动态任务触发器";  
    public static String JOB_GROUP_NAME = "XLXXCC_JOB_GROUP";  
    public static String TRIGGER_GROUP_NAME = "XLXXCC_JOB_GROUP"; 

    @Test
    public void scheduleTest() {
        try {  
            JobScheduleInfo scheduleInfo = new JobScheduleInfo();
            scheduleInfo.setJobName(JOB_NAME);
            scheduleInfo.setJobGroupName(JOB_GROUP_NAME);
            scheduleInfo.setTriggerName(TRIGGER_NAME);
            scheduleInfo.setTriggerGroupName(TRIGGER_GROUP_NAME);
            scheduleInfo.setCron("0/1 * * * * ?");
            
            System.out.println("【系统启动】开始(每1秒输出一次)...");    
            JobScheduler.addJob(scheduleInfo,UserJob.class); 

            Thread.sleep(5000);    
            System.out.println("【修改时间】开始(每5秒输出一次)...");   
            scheduleInfo.setCron("0/5 * * * * ?");
            JobScheduler.modifyJobTime(scheduleInfo);    

            Thread.sleep(6000);    
            System.out.println("【移除定时】开始...");    
            JobScheduler.removeJob(scheduleInfo);    
            System.out.println("【移除定时】成功");  
            
            CronTriggerImpl cronTrigger = new CronTriggerImpl(); 
            cronTrigger.setCronExpression("0 59 2 ? * FRI"); 
            System.out.println(cronTrigger.getNextFireTime());
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
}
