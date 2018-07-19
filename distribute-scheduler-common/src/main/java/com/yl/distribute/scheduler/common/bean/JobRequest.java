package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import java.util.Map;

public class JobRequest implements Serializable{

    private static final long serialVersionUID = 1L;
    private String requestId;
    private String command;    
    private String poolName;
    private int retryTimes = 1;
    private Map<String,Object> commandParameters;
    private Map<String,Object> executeParameters;       
    
    public int getRetryTimes() {
        return retryTimes;
    }
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
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