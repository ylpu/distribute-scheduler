package com.yl.distribute.scheduler.common.bean;

import java.util.Date;

public class JobFlow {
    
    private String id;
    private JobPlan jobPlan;
    private String status;
    private Date startDate;
    private Date endDate;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public JobPlan getJobPlan() {
        return jobPlan;
    }
    public void setJobPlan(JobPlan jobPlan) {
        this.jobPlan = jobPlan;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }      
}
