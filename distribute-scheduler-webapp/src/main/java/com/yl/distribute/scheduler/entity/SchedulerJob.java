package com.yl.distribute.scheduler.entity;

import java.io.Serializable;
import java.util.Date;

public class SchedulerJob implements Serializable {
    private Integer id;

    private String jobId;

    private String jobName;

    private String jobDesc;

    private String jobType;

    private String poolPath;

    private Integer retryTimes;

    private String jobStrategy;

    private String cronExpression;

    private String command;

    private String alertEmail;

    private String owner;

    private String resourceParameters;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId == null ? null : jobId.trim();
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName == null ? null : jobName.trim();
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc == null ? null : jobDesc.trim();
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType == null ? null : jobType.trim();
    }

    public String getPoolPath() {
        return poolPath;
    }

    public void setPoolPath(String poolPath) {
        this.poolPath = poolPath == null ? null : poolPath.trim();
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getJobStrategy() {
        return jobStrategy;
    }

    public void setJobStrategy(String jobStrategy) {
        this.jobStrategy = jobStrategy == null ? null : jobStrategy.trim();
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression == null ? null : cronExpression.trim();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command == null ? null : command.trim();
    }

    public String getAlertEmail() {
        return alertEmail;
    }

    public void setAlertEmail(String alertEmail) {
        this.alertEmail = alertEmail == null ? null : alertEmail.trim();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getResourceParameters() {
        return resourceParameters;
    }

    public void setResourceParameters(String resourceParameters) {
        this.resourceParameters = resourceParameters == null ? null : resourceParameters.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}