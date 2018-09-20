package com.yl.distribute.scheduler.common.bean;

import java.sql.Clob;

public class WorkPlan {
    
    private Integer id;
    private String planName;
    private String cron;    
    private Clob jobPlanFile;
    
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
    public String getCron() {
        return cron;
    }
    public void setCron(String cron) {
        this.cron = cron;
    }   
}
