package com.yl.distribute.scheduler.web.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.enums.JobType;
import com.yl.distribute.scheduler.common.enums.JobStrategy;

public class JobRepository {
    
    private static JobRepository jobManager = new JobRepository();
    
    private static Map<String,JobRequest> jobMap = new ConcurrentHashMap<String,JobRequest>();
    
    static {
        JobRequest joba = new JobRequest();
        
        joba.setJobId("a");
        joba.setJobType(JobType.COMMAND);
        joba.setCommand("cmd /c echo abc");
        joba.setPoolPath("/root/pool1");
        joba.setJobStrategy(JobStrategy.MEMORY);
        jobMap.put(joba.getJobId(), joba);
        
        JobRequest jobb = new JobRequest();
        jobb.setJobId("b");
        jobb.setJobType(JobType.COMMAND);
        jobb.setCommand("cmd /c echo abc");
        jobb.setPoolPath("/root/pool1");
        jobb.setJobStrategy(JobStrategy.MEMORY);
        jobMap.put(jobb.getJobId(), jobb);
        
        JobRequest jobc = new JobRequest();
        jobc.setJobId("c");
        jobc.setJobType(JobType.COMMAND);
        jobc.setCommand("cmd /c echo abc");
        jobc.setPoolPath("/root/pool1");
        jobc.setJobStrategy(JobStrategy.MEMORY);
        jobMap.put(jobc.getJobId(), jobc);
        
        JobRequest jobd = new JobRequest();
        jobd.setJobId("d");
        jobd.setJobType(JobType.COMMAND);
        jobd.setCommand("cmd /c echo abc");
        jobd.setPoolPath("/root/pool1");
        jobd.setJobStrategy(JobStrategy.MEMORY);
        jobMap.put(jobd.getJobId(), jobd);
    }
    
    private JobRepository() { }
    

    
    public static JobRepository getInstance() {
        return jobManager;
    }
    
    public JobRequest getJobById(String jobId) {
        return jobMap.get(jobId);
    }

    public void addJob(JobRequest job) {
        jobMap.put(job.getJobId(), job);
    }
}
