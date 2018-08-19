package com.yl.distribute.scheduler.resource.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceStrategy {
    
    private static Map<String,ServerSelectStrategy> strategyMap = new ConcurrentHashMap<String,ServerSelectStrategy>();
    
    static{
        strategyMap.put("memory", new ResourceIdleStrategy());
        strategyMap.put("task", new TaskIdleStrategy());
        strategyMap.put("random", new RandomStrategy());
    }
    
    public static void addStrategy(String key ,ServerSelectStrategy serverSelectStrategy){
        strategyMap.put(key, serverSelectStrategy);
    }
    
    public static ServerSelectStrategy getStrategy(String key){  
        ServerSelectStrategy strategy = strategyMap.get(key);
        if(strategy == null){
            return new RandomStrategy();
        }
        return strategy;
    }

}
