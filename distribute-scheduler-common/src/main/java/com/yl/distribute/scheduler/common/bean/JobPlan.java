package com.yl.distribute.scheduler.common.bean;

import java.sql.Clob;

public class JobPlan {
    
    private String id;
    private Clob jobPlanFile;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Clob getJobPlanFile() {
        return jobPlanFile;
    }
    public void setJobPlanFile(Clob jobPlanFile) {
        this.jobPlanFile = jobPlanFile;
    }
}
