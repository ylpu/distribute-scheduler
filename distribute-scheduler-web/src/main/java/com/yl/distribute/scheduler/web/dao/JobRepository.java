package com.yl.distribute.scheduler.web.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.enums.JobType;
import com.yl.distribute.scheduler.common.enums.TaskStrategy;

public class JobRepository {
    
    private static JobRepository jobManager = new JobRepository();
    
    private static Map<String,JobConf> jobMap = new ConcurrentHashMap<String,JobConf>();
    
    static {
        JobConf joba = new JobConf();
        
        joba.setJobId("a");
        joba.setJobType(JobType.COMMAND);
        joba.setCommand("cmd /c echo abc");
        joba.setPoolPath("/root/pool1");
        joba.setTaskStrategy(TaskStrategy.MEMORY);
        jobMap.put(joba.getJobId(), joba);
        
        JobConf jobb = new JobConf();
        jobb.setJobId("b");
        jobb.setJobType(JobType.COMMAND);
        jobb.setCommand("cmd /c echo abc");
        jobb.setPoolPath("/root/pool1");
        jobb.setTaskStrategy(TaskStrategy.MEMORY);
        jobMap.put(jobb.getJobId(), jobb);
        
        JobConf jobc = new JobConf();
        jobc.setJobId("c");
        jobc.setJobType(JobType.COMMAND);
        jobc.setCommand("cmd /c echo abc");
        jobc.setPoolPath("/root/pool1");
        jobc.setTaskStrategy(TaskStrategy.MEMORY);
        jobMap.put(jobc.getJobId(), jobc);
        
        JobConf jobd = new JobConf();
        jobd.setJobId("d");
        jobd.setJobType(JobType.COMMAND);
        jobd.setCommand("cmd /c echo abc");
        jobd.setPoolPath("/root/pool1");
        jobd.setTaskStrategy(TaskStrategy.MEMORY);
        jobMap.put(jobd.getJobId(), jobd);
    }
    
    private JobRepository() { }
    

    
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
