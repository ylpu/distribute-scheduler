package com.yl.distribute.scheduler.common.bean;

public class JobScheduleInfo {
    
    private String jobName;
    private String jobGroupName;
    private String triggerName;
    private String triggerGroupName;
    private String cron;
    private Object data;
    
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getJobGroupName() {
        return jobGroupName;
    }
    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }
    public String getTriggerName() {
        return triggerName;
    }
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }
    public String getTriggerGroupName() {
        return triggerGroupName;
    }
    public void setTriggerGroupName(String triggerGroupName) {
        this.triggerGroupName = triggerGroupName;
    }
    public String getCron() {
        return cron;
    }
    public void setCron(String cron) {
        this.cron = cron;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

}
