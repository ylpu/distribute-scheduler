package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import java.util.Date;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class TaskRequest implements Serializable{

    private static final long serialVersionUID = 1L;
	private String id;
    private String taskId;
    private JobConf job;
    private int failedTimes = 0;
    private String runningServer;   
    private String lastFailedServer;
    private TaskStatus taskStatus;
    private String errorOutputUrl;
    private String stdOutputUrl;
    private Date startTime;
    private Date endTime;    
    
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
    public JobConf getJob() {
        return job;
    }
    public void setJob(JobConf job) {
        this.job = job;
    }
    public int getFailedTimes() {
        return failedTimes;
    }
    public void setFailedTimes(int failedTimes) {
        this.failedTimes = failedTimes;
    }
    public String getRunningServer() {
        return runningServer;
    }
    public void setRunningServer(String runningServer) {
        this.runningServer = runningServer;
    }	
    public String getLastFailedServer() {
        return lastFailedServer;
    }
    public void setLastFailedServer(String lastFailedServer) {
        this.lastFailedServer = lastFailedServer;
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
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
