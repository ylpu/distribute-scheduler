package com.yl.distribute.scheduler.resource.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yl.distribute.scheduler.common.enums.TaskStrategy;

public class ResourceStrategy {
    
    private static Map<TaskStrategy,HostSelectStrategy> strategyMap = new ConcurrentHashMap<TaskStrategy,HostSelectStrategy>();
    
    static{
        strategyMap.put(TaskStrategy.MEMORY, new ResourceIdleStrategy());
        strategyMap.put(TaskStrategy.TASK, new TaskIdleStrategy());
        strategyMap.put(TaskStrategy.RANDOM, new RandomStrategy());
    }
    
    public static void addStrategy(TaskStrategy key ,HostSelectStrategy serverSelectStrategy){
        strategyMap.put(key, serverSelectStrategy);
    }
    
    public static HostSelectStrategy getStrategy(TaskStrategy taskStrategy){  
        HostSelectStrategy strategy = strategyMap.get(taskStrategy);
        if(strategy == null){
            return new RandomStrategy();
        }
        return strategy;
    }
}
