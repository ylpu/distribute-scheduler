package com.yl.distribute.scheduler.common.bean;

import java.util.List;

public class JobConf {
    
    private String jobId;
    private List<JobRequest> parentJobs;
    private List<JobRequest> childJobs;
    
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public List<JobRequest> getParentJobs() {
        return parentJobs;
    }
    public void setParentJobs(List<JobRequest> parentJobs) {
        this.parentJobs = parentJobs;
    }
    public List<JobRequest> getChildJobs() {
        return childJobs;
    }
    public void setChildJobs(List<JobRequest> childJobs) {
        this.childJobs = childJobs;
    }    
}
