package com.yl.distribute.scheduler.resource.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yl.distribute.scheduler.common.enums.JobStrategy;

public class ResourceStrategy {
    
    private static Map<JobStrategy,HostSelectStrategy> strategyMap = new ConcurrentHashMap<JobStrategy,HostSelectStrategy>();
    
    static{
        strategyMap.put(JobStrategy.MEMORY, new ResourceIdleStrategy());
        strategyMap.put(JobStrategy.TASK, new TaskIdleStrategy());
        strategyMap.put(JobStrategy.RANDOM, new RandomStrategy());
    }
    
    public static void addStrategy(JobStrategy key ,HostSelectStrategy serverSelectStrategy){
        strategyMap.put(key, serverSelectStrategy);
    }
    
    public static HostSelectStrategy getStrategy(JobStrategy taskStrategy){  
        HostSelectStrategy strategy = strategyMap.get(taskStrategy);
        if(strategy == null){
            return new RandomStrategy();
        }
        return strategy;
    }
}
