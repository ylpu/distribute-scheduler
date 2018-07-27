package com.yl.distribute.scheduler.resource.manager;

import com.yl.distribute.scheduler.common.bean.JobRequest;

public class ResourceStrategyContext {
    
    private ServerSelectStrategy strategy;
    
    public ResourceStrategyContext(ServerSelectStrategy strategy) {
        this.strategy = strategy;
    }
    
    public String select(JobRequest input,String lastFailedServer,ResourceManager rm) {
        return strategy.getIdleServer(input,lastFailedServer,rm);
    }
}
