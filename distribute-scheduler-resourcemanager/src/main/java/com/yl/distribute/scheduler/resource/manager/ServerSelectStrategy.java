package com.yl.distribute.scheduler.resource.manager;

import java.util.List;
import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;

public interface ServerSelectStrategy {
    
    public String getIdleServer(JobRequest input,Map<String,List<String>> poolServers,Map<String,HostInfo> resourceMap,String lastFailedServer);

}
