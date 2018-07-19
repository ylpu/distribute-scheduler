package com.yl.distribute.scheduler.web.service;

import javax.enterprise.context.ApplicationScoped;

import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.web.job.JobManager;

@ApplicationScoped
public class JobService {
    
    public void updateJob(JobResponse response) {
        JobManager.getInstance().updateJob(response);
    }
    
    public String getLastFailedServer(String jobId) { 
        return JobManager.getInstance().getLastFailedServer(jobId);      
    }
}
