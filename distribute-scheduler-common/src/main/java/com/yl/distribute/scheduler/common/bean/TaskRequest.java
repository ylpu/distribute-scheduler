package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import java.util.Date;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

/**
 * 根据job生成相关task
 *
 */
public class TaskRequest implements Serializable{

    private static final long serialVersionUID = 1L;
	private Integer id;	
    private String taskId;
    private Integer flowId;
    private JobConf job;
    private int failedTimes = 0;
    private String runningHost;   
    private String lastFailedHost;
    private TaskStatus taskStatus;
    private String errorOutputUrl;
    private String stdOutputUrl;
    private Date startTime;
    private Date endTime;
    private long elapseTime;
    
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
	public Integer getFlowId() {
		return flowId;
	}
	public void setFlowId(Integer flowId) {
		this.flowId = flowId;
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
    public String getRunningHost() {
        return runningHost;
    }
    public void setRunningHost(String runningHost) {
        this.runningHost = runningHost;
    }	
    public String getLastFailedHost() {
        return lastFailedHost;
    }
    public void setLastFailedHost(String lastFailedHost) {
        this.lastFailedHost = lastFailedHost;
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
    public long getElapseTime() {
        return elapseTime;
    }
    public void setElapseTime(long elapseTime) {
        this.elapseTime = elapseTime;
    }    
}
