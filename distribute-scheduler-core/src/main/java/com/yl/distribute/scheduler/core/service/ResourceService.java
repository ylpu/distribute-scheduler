package com.yl.distribute.scheduler.core.service;

import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;

public interface ResourceService {
    
    String getIdleServer(JobRequest input,String lastFailedServer);
    
    void addResource(String serverName,Map<String,Object> resourceParams);
    
    void subResource(String serverName,Map<String,Object> resourceParams);
    
    Map<String,HostInfo> getResources();

}
