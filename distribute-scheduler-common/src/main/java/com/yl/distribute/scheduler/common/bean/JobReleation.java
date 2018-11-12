package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JobReleation implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String jobId;
    private List<JobRequest> parentJobs = new ArrayList<JobRequest>();
    private List<JobRequest> childJobs = new ArrayList<JobRequest>();
    
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
