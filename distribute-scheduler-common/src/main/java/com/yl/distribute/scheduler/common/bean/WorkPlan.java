package com.yl.distribute.scheduler.common.bean;

import java.sql.Clob;
import java.util.Date;

public class WorkPlan {
    
    private Integer id;
    private String planName;
    private Clob jobPlanFile;
    private Date createTime;
    private Date updateTime;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Clob getJobPlanFile() {
        return jobPlanFile;
    }
    public void setJobPlanFile(Clob jobPlanFile) {
        this.jobPlanFile = jobPlanFile;
    }
    public String getPlanName() {
        return planName;
    }
    public void setPlanName(String planName) {
        this.planName = planName;
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
