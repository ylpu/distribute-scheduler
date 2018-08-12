package com.yl.distribute.scheduler.web.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.yl.distribute.scheduler.common.bean.JobConf;

public class JobRepository {
    
    private static JobRepository jobManager = new JobRepository();
    
    private static Map<String,JobConf> jobMap = new ConcurrentHashMap<String,JobConf>();
    
    private JobRepository() {        
    }
    
    public static JobRepository getInstance() {
        return jobManager;
    }
    
    public JobConf getJobById(String jobId) {
        return jobMap.get(jobId);
    }

    public void addJob(JobConf job) {
        jobMap.put(job.getJobId(), job);
    }
}
