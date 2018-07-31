package com.yl.distribute.scheduler.server.processor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yl.distribute.scheduler.common.enums.JobType;

public class ProcessorManager {
    
    private static Map<JobType, Class<? extends IServerProcessor>> map = new ConcurrentHashMap<JobType,Class<? extends IServerProcessor>>();

    static {
        map.put(JobType.COMMAND, CommandProcessor.class);
        map.put(JobType.JAR, JarProcessor.class);
    }
    
    public static Class<?> getProcessor(JobType jobType) {
        if(jobType == null || map.get(jobType) == null) {
            throw new RuntimeException("does not support jobType " + jobType);
        }
        return map.get(jobType);
    }    
}
