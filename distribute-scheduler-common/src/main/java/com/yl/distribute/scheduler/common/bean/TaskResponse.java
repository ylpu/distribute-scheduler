package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

public class TaskResponse implements Serializable{

    private static final long serialVersionUID = 1L;
    private String taskId;
    private String taskStatus;
    private String errorOutputUrl;
    private String stdOutputUrl;
    
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
    public String getErrorOutputUrl() {
        return errorOutputUrl;
    }
    public void setErrorOutputUrl(String errorOutputUrl) {
        this.errorOutputUrl = errorOutputUrl;
    }
    public String getStdOutputUrl() {
        return stdOutputUrl;
    }
    public void setStdOutputUrl(String stdOutputUrl) {
        this.stdOutputUrl = stdOutputUrl;
    }       
}
