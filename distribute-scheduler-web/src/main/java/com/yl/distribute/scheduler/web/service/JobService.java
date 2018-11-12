package com.yl.distribute.scheduler.web.service;

import com.yl.distribute.scheduler.common.bean.JobRequest;

public interface JobService {
    
    public JobRequest getJobById(String jobId);
    
    public void addJob(JobRequest job);

}
