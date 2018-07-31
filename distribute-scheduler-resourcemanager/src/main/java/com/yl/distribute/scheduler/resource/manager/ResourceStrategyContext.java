package com.yl.distribute.scheduler.resource.manager;

import com.yl.distribute.scheduler.common.bean.JobConf;

public class ResourceStrategyContext {
    
    private ServerSelectStrategy strategy;
    
    public ResourceStrategyContext(ServerSelectStrategy strategy) {
        this.strategy = strategy;
    }
    
    public String select(ResourceManager rm,JobConf input,String... lastFailedServers) {
        return strategy.getIdleServer(rm,input,lastFailedServers);
    }
}
