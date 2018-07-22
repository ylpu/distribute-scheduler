package com.yl.distribute.scheduler.web.service;

import javax.enterprise.context.ApplicationScoped;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.web.job.JobManager;

@ApplicationScoped
public class JobServiceImpl implements JobService{
    
    public void updateJob(JobResponse response) {
        JobManager.getInstance().updateJob(response);
    }
    
    public void insertJob(JobResponse response) {
        JobManager.getInstance().addJob(response);
    }
    
    public JobResponse getJobById(String jobId) {
        return JobManager.getInstance().getJob(jobId);
    }    
    
    public String getLastFailedServer(String jobId) { 
        return JobManager.getInstance().getLastFailedServer(jobId);      
    }
    
    public String getErrorLog(String jobId) {
        return JobManager.getInstance().getJob(jobId).getErrorOutputUrl();
    }
    
    public String getOutputLog(String jobId) {
        return JobManager.getInstance().getJob(jobId).getStdOutputUrl();
    }
}
