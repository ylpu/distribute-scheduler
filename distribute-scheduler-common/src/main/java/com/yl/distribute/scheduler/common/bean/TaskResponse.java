package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class TaskResponse implements Serializable{

    private static final long serialVersionUID = 1L;
    private String id;
    private String taskId;
    private TaskStatus taskStatus;
    
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
}
