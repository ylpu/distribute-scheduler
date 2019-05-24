package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import java.util.Date;

import com.yl.distribute.scheduler.common.enums.JobType;
import com.yl.distribute.scheduler.common.enums.JobStrategy;

public class JobRequest implements Serializable,Comparable<JobRequest>{

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String jobId;
    private String jobName;
    private String jobDesc;
    private JobType jobType;        
    private String poolPath;
    private int retryTimes = 0;
    private int retryInterval = 5;
    private JobStrategy jobStrategy;
    private String cronExpression;    
    private String command;
    private String alertEmail;
    private String owner;
    private String resourceParameters;
    private Date createTime;
    private Date updateTime;
    private JobReleation jobReleation = new JobReleation();    
    
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getRetryTimes() {
        return retryTimes;
    }
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }        
    public String getCronExpression() {
        return cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }   
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }    
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }    
    public String getJobDesc() {
		return jobDesc;
	}
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }    
    public JobType getJobType() {
        return jobType;
    }
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
    public String getPoolPath() {
        return poolPath;
    }
    public void setPoolPath(String poolPath) {
        this.poolPath = poolPath;
    }	
	public JobStrategy getJobStrategy() {
		return jobStrategy;
	}
	public void setJobStrategy(JobStrategy jobStrategy) {
		this.jobStrategy = jobStrategy;
	}
	public String getAlertEmail() {
		return alertEmail;
	}
	public void setAlertEmail(String alertEmail) {
		this.alertEmail = alertEmail;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public JobReleation getJobReleation() {
        return jobReleation;
    }
    public String getResourceParameters() {
		return resourceParameters;
	}
	public void setResourceParameters(String resourceParameters) {
		this.resourceParameters = resourceParameters;
	}
	public void setJobReleation(JobReleation jobReleation) {
        this.jobReleation = jobReleation;
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
	public int getRetryInterval() {
		return retryInterval;
	}
	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}
	@Override
    public int compareTo(JobRequest o) {
        return (this.getJobName().compareTo(o.getJobName()));
    }    
}