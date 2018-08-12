package com.yl.distribute.scheduler.web.service;

import com.yl.distribute.scheduler.common.bean.JobConf;

public interface JobService {
    
    public JobConf getJobById(String jobId);
    
    public void addJob(JobConf job);

}
