package com.yl.distribute.scheduler.entity;

import java.io.Serializable;
import java.util.Date;

public class SchedulerTask implements Serializable {
    private Integer id;

    private String taskId;

    private Integer flowId;

    private String jobId;

    private Integer failedTimes;

    private String runningHost;

    private String lastfailedhost;

    private String taskStatus;

    private String errorOutputUrl;

    private String stdOutputUrl;

    private Date startTime;

    private Date endTime;

    private Long elapseTime;

    private static final long serialVersionUID = 1L;

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
        this.taskId = taskId == null ? null : taskId.trim();
    }

    public Integer getFlowId() {
        return flowId;
    }

    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId == null ? null : jobId.trim();
    }

    public Integer getFailedTimes() {
        return failedTimes;
    }

    public void setFailedTimes(Integer failedTimes) {
        this.failedTimes = failedTimes;
    }

    public String getRunningHost() {
        return runningHost;
    }

    public void setRunningHost(String runningHost) {
        this.runningHost = runningHost == null ? null : runningHost.trim();
    }

    public String getLastfailedhost() {
        return lastfailedhost;
    }

    public void setLastfailedhost(String lastfailedhost) {
        this.lastfailedhost = lastfailedhost == null ? null : lastfailedhost.trim();
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus == null ? null : taskStatus.trim();
    }

    public String getErrorOutputUrl() {
        return errorOutputUrl;
    }

    public void setErrorOutputUrl(String errorOutputUrl) {
        this.errorOutputUrl = errorOutputUrl == null ? null : errorOutputUrl.trim();
    }

    public String getStdOutputUrl() {
        return stdOutputUrl;
    }

    public void setStdOutputUrl(String stdOutputUrl) {
        this.stdOutputUrl = stdOutputUrl == null ? null : stdOutputUrl.trim();
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

    public Long getElapseTime() {
        return elapseTime;
    }

    public void setElapseTime(Long elapseTime) {
        this.elapseTime = elapseTime;
    }
}