package com.yl.distribute.scheduler.core.service;

import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;

public interface ResourceService {
    
    String getIdleServer(JobRequest input,String lastFailedServer);
    
    void addResource(String serverName,Map<String,Object> resourceParams);
    
    void subResource(String serverName,Map<String,Object> resourceParams);
    
    void increaseTask(String serverName);
    
    void decreaseTask(String serverName);
    
    Map<String,HostInfo> getResources();
    
    Map<String,Integer> getTasks();
}
