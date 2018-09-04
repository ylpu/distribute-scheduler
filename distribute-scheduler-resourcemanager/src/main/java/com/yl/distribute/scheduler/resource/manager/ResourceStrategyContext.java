package com.yl.distribute.scheduler.resource.manager;

import com.yl.distribute.scheduler.common.bean.JobConf;

public class ResourceStrategyContext {
    
    private HostSelectStrategy strategy;
    
    public ResourceStrategyContext(HostSelectStrategy strategy) {
        this.strategy = strategy;
    }
    
    public String select(ResourceManager rm,JobConf input,String... lastFailedHosts) {
        return strategy.getIdleHost(rm,input,lastFailedHosts);
    }
}
