package com.yl.distribute.scheduler.service;

import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.service.BaseService;
import com.yl.distribute.scheduler.entity.SchedulerJob;

public interface JobService extends BaseService<SchedulerJob,String>{

    public void addJob(JobRequest job);

    public JobRequest getJobById(String id);

}
