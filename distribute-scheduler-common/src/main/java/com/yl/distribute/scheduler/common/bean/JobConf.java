package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import com.yl.distribute.scheduler.common.enums.JobType;
import com.yl.distribute.scheduler.common.enums.TaskStrategy;

public class JobConf implements Serializable,Comparable<JobConf>{

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String jobId;
    private String jobName;
    private String jobDesc;
    private JobType jobType;        
    private String poolPath;
    private int retryTimes = 0;
    private TaskStrategy taskStrategy;
    private String cronExpression;    
    private String command;
    private String resourceParameters;  
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
	public TaskStrategy getTaskStrategy() {
		return taskStrategy;
	}
	public void setTaskStrategy(TaskStrategy taskStrategy) {
		this.taskStrategy = taskStrategy;
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
    @Override
    public int compareTo(JobConf o) {
        return (this.getJobName().compareTo(o.getJobName()));
    }    
}