package com.yl.distribute.scheduler.client.job;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey; 
import org.quartz.SchedulerFactory;  
import org.quartz.impl.StdSchedulerFactory;
import com.yl.distribute.scheduler.common.bean.JobScheduleInfo;

public class JobScheduler {
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();  
    
    /**
     * 动态添加任务
     * @param scheduleInfo
     * @param jobClass
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })  
    public static void addJob(JobScheduleInfo scheduleInfo, Class jobClass) {  
        try {  
            Scheduler sched = schedulerFactory.getScheduler();  
            // 任务名，任务组，任务执行类
            JobDetail jobDetail= JobBuilder.newJob(jobClass).withIdentity(scheduleInfo.getJobName(), scheduleInfo.getJobGroupName()).build();
            jobDetail.getJobDataMap().put("data", scheduleInfo.getData());
            // 触发器  
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组  
            triggerBuilder.withIdentity(scheduleInfo.getTriggerName(), scheduleInfo.getTriggerGroupName());
            triggerBuilder.startNow();
            // 触发器时间设定  
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(scheduleInfo.getCron()));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();

            // 调度容器设置JobDetail和Trigger
            sched.scheduleJob(jobDetail, trigger);  

            // 启动  
            if (!sched.isShutdown()) {  
                sched.start();  
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  

    /**
     * 动态修改任务时间
     * @param scheduleInfo
     */
    public static void modifyJobTime(JobScheduleInfo scheduleInfo) {  
        try {  
            Scheduler sched = schedulerFactory.getScheduler();  
            TriggerKey triggerKey = TriggerKey.triggerKey(scheduleInfo.getTriggerName(), scheduleInfo.getTriggerGroupName());
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);  
            if (trigger == null) {  
                return;  
            }  

            String oldTime = trigger.getCronExpression();  
            if (!oldTime.equalsIgnoreCase(scheduleInfo.getCron())) { 
                // 触发器  
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组  
                triggerBuilder.withIdentity(scheduleInfo.getTriggerName(), scheduleInfo.getTriggerGroupName());
                triggerBuilder.startNow();
                // 触发器时间设定  
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(scheduleInfo.getCron()));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改一个任务的触发时间
                sched.rescheduleJob(triggerKey, trigger);
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  

    /**
     * 动态删除任务
     * @param scheduleInfo
     */
    public static void removeJob(JobScheduleInfo scheduleInfo) {  
        try {  
            Scheduler sched = schedulerFactory.getScheduler();  

            TriggerKey triggerKey = TriggerKey.triggerKey(scheduleInfo.getTriggerName(), scheduleInfo.getTriggerGroupName());

            sched.pauseTrigger(triggerKey);// 停止触发器  
            sched.unscheduleJob(triggerKey);// 移除触发器  
            sched.deleteJob(JobKey.jobKey(scheduleInfo.getJobName(), scheduleInfo.getJobGroupName()));// 删除任务  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  

    /** 
     * @Description:启动所有定时任务 
     */  
    public static void startJobs() {  
        try {  
            Scheduler sched = schedulerFactory.getScheduler();  
            sched.start();  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  

    /** 
     * @Description:关闭所有定时任务 
     */  
    public static void shutdownJobs() {  
        try {  
            Scheduler sched = schedulerFactory.getScheduler();  
            if (!sched.isShutdown()) {  
                sched.shutdown();  
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
}
