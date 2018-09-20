package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class TaskResponse implements Serializable{

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String taskId;
    private String jobId;
    private int failedTimes = 0;
    private TaskStatus taskStatus;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public int getFailedTimes() {
        return failedTimes;
    }
    public void setFailedTimes(int failedTimes) {
        this.failedTimes = failedTimes;
    }
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }    
}
