package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import java.util.Map;

public class JobRequest implements Serializable{

    private static final long serialVersionUID = 1L;
    private String jobId;
    private String command;    
    private String poolName;
    private int retryTimes = 1;
    private int failedTimes = 0;
    private Map<String,Object> commandParameters;
    private Map<String,Object> executeParameters;       
    
    public int getRetryTimes() {
        return retryTimes;
    }
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }     
    public int getFailedTimes() {
        return failedTimes;
    }
    public void setFailedTimes(int failedTimes) {
        this.failedTimes = failedTimes;
    }    
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }     
    public String getPoolName() {
        return poolName;
    }
    public void setPoolName(String poolName) {
        this.poolName = poolName;
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