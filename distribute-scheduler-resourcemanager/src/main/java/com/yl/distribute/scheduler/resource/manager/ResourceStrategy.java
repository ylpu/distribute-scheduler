package com.yl.distribute.scheduler.resource.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceStrategy {
    
    private static Map<String,HostSelectStrategy> strategyMap = new ConcurrentHashMap<String,HostSelectStrategy>();
    
    static{
        strategyMap.put("memory", new ResourceIdleStrategy());
        strategyMap.put("task", new TaskIdleStrategy());
        strategyMap.put("random", new RandomStrategy());
    }
    
    public static void addStrategy(String key ,HostSelectStrategy serverSelectStrategy){
        strategyMap.put(key, serverSelectStrategy);
    }
    
    public static HostSelectStrategy getStrategy(String key){  
        HostSelectStrategy strategy = strategyMap.get(key);
        if(strategy == null){
            return new RandomStrategy();
        }
        return strategy;
    }

}
