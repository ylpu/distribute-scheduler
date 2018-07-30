package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class TaskResponse implements Serializable{

    private static final long serialVersionUID = 1L;
    private String id;
    private String taskId;
    private TaskStatus taskStatus;
    private String errorOutputUrl;
    private String stdOutputUrl;   
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(TaskStatus taskStatus) {
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
