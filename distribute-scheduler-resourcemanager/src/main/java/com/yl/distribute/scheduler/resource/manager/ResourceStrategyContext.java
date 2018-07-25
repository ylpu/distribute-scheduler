package com.yl.distribute.scheduler.resource.manager;

import java.util.List;
import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;

public class ResourceStrategyContext {
    
    private ServerSelectStrategy strategy;
    
    public ResourceStrategyContext(ServerSelectStrategy strategy) {
        this.strategy = strategy;
    }
    
    public String select(JobRequest input,Map<String,List<String>> poolServers,Map<String,HostInfo> resourceMap,String lastFailedServer) {
        return strategy.getIdleServer(input, poolServers, resourceMap, lastFailedServer);
    }

}
