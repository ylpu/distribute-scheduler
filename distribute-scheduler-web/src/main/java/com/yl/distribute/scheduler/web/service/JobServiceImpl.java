package com.yl.distribute.scheduler.web.service;

import org.springframework.stereotype.Component;

import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.web.dao.JobRepository;
@Component
public class JobServiceImpl implements JobService{

    @Override
    public JobRequest getJobById(String jobId) {
        return JobRepository.getInstance().getJobById(jobId);
    }

    @Override
    public void addJob(JobRequest job) {
        JobRepository.getInstance().addJob(job);
    }
}
