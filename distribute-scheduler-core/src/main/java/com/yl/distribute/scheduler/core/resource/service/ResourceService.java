package com.yl.distribute.scheduler.core.resource.service;

import java.util.Map;

import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.bean.TaskRequest;

public interface ResourceService {
    
    String getIdleHost(JobRequest input,String... lastFailedHosts);
    
    void addResource(String serverName,TaskRequest taskRequest);
    
    void subResource(String serverName,TaskRequest taskRequest);
    
    Map<String,HostInfo> getResources();
    
    Map<String,Integer> getTasks();
}
