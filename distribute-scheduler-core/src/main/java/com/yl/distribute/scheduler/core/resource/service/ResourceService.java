package com.yl.distribute.scheduler.core.resource.service;

import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobConf;

public interface ResourceService {
    
    String getIdleHost(JobConf input,String... lastFailedHosts);
    
    void addResource(String serverName,JobConf jobConf);
    
    void subResource(String serverName,JobConf jobConf);
    
    void increaseTask(String serverName);
    
    void decreaseTask(String serverName);
    
    Map<String,HostInfo> getResources();
    
    Map<String,Integer> getTasks();
}
