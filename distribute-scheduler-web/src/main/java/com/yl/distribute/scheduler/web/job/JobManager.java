package com.yl.distribute.scheduler.web.job;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.common.enums.JobStatus;

public class JobManager {
    
    private Map<String,JobResponse> jobMap = new ConcurrentHashMap<String,JobResponse>();
    private static JobManager jobManager = new JobManager();
    
    private JobManager() {        
    }
    
    public static JobManager getInstance() {
        return jobManager;
    }    
    
    public void addJob(String jobId,JobResponse response) {
        jobMap.put(jobId, response);
    }
    
    public void removeJob(String jobId) {
        jobMap.remove(jobId);
    }
    
    public void updateJob(JobResponse response) {
        removeJob(response.getResponseId());
        addJob(response.getResponseId(), response);
    }
    
    public JobResponse getJob(String jobId) {
        return jobMap.get(jobId);
    }
    
    public String getLastFailedServer(String jobId) {
        return (jobMap.get(jobId) != null && jobMap.get(jobId).getJobStatus().equalsIgnoreCase(JobStatus.FAILED.getStatus()))
                ? jobMap.get(jobId).getRunningServer(): "";
    }
}