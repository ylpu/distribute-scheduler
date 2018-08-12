package com.yl.distribute.scheduler.web.service;

import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.web.dao.JobRepository;

public class JobServiceImpl implements JobService{

    @Override
    public JobConf getJobById(String jobId) {
        return JobRepository.getInstance().getJobById(jobId);
    }

    @Override
    public void addJob(JobConf job) {
        JobRepository.getInstance().addJob(job);
    }
}
