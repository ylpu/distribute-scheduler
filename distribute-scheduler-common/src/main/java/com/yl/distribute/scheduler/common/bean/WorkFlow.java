package com.yl.distribute.scheduler.common.bean;

import java.util.Date;

/**
 * 每次执行jobplan生成一个实例
 * @author asus
 *
 */
public class WorkFlow {
    
    private Integer id;
    private WorkPlan jobPlan;
    private String status;
    private Date startTime;
    private Date endTime;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public WorkPlan getJobPlan() {
        return jobPlan;
    }
    public void setJobPlan(WorkPlan jobPlan) {
        this.jobPlan = jobPlan;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
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
