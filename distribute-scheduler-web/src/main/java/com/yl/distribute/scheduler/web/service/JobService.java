package com.yl.distribute.scheduler.web.service;

import com.yl.distribute.scheduler.common.bean.JobResponse;

public interface JobService {
	
    public void updateJob(JobResponse response) ;
    
    public void insertJob(JobResponse response);
    
    public JobResponse getJobById(String jobId);
    
    public String getLastFailedServer(String jobId);
    
    public String getErrorLog(String jobId);
    
    public String getOutputLog(String jobId);
}
