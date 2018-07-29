package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import java.util.Map;

public class JobConf implements Serializable{

    private static final long serialVersionUID = 1L;
    private String jobId;
    private String jobName;
    private String command;   
    private String commandType; 
    private String poolPath;
    private int retryTimes = 3;
    private String cronExpression;    
    private Map<String,Object> commandParameters;
    private Map<String,Object> executeParameters;       
    
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
    public String getCommandType() {
        return commandType;
    }
    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }
    public String getPoolPath() {
        return poolPath;
    }
    public void setPoolPath(String poolPath) {
        this.poolPath = poolPath;
    }
    public Map<String, Object> getCommandParameters() {
        return commandParameters;
    }
    public void setCommandParameters(Map<String, Object> commandParameters) {
        this.commandParameters = commandParameters;
    }
    public Map<String, Object> getExecuteParameters() {
        return executeParameters;
    }
    public void setExecuteParameters(Map<String, Object> executeParameters) {
        this.executeParameters = executeParameters;
    }  
}