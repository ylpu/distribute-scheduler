package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import com.yl.distribute.scheduler.common.enums.JobType;

public class JobConf implements Serializable{

    private static final long serialVersionUID = 1L;
    private String jobId;
    private String jobName;
    private String command;   
    private JobType jobType; 
    private String poolPath;
    private int retryTimes = 0;
    private String cronExpression;    
    private String commandParameters;
    private String executeParameters;       
    
    public int getRetryTimes() {
        return retryTimes;
    }
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }        
    public String getCronExpression() {
        return cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }   
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }    
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }    
    public JobType getJobType() {
        return jobType;
    }
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
    public String getPoolPath() {
        return poolPath;
    }
    public void setPoolPath(String poolPath) {
        this.poolPath = poolPath;
    }
    public String getCommandParameters() {
        return commandParameters;
    }
    public void setCommandParameters(String commandParameters) {
        this.commandParameters = commandParameters;
    }
    public String getExecuteParameters() {
        return executeParameters;
    }
    public void setExecuteParameters(String executeParameters) {
        this.executeParameters = executeParameters;
    }
}