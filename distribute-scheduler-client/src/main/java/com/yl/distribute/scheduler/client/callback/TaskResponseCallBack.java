package com.yl.distribute.scheduler.client.callback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.yl.distribute.scheduler.common.bean.TaskResponse;

public class TaskResponseCallBack {
    
    private static Map<String,TaskResponse> taskResponseMap = new ConcurrentHashMap<String,TaskResponse>();
    
    public static void add(String jobId,TaskResponse response) {
        taskResponseMap.put(jobId, response);
    }
    
    public static void remove(String jobId) {
        taskResponseMap.remove(jobId);
    }
    
    public static TaskResponse get(String jobId) {
        return taskResponseMap.get(jobId);
    }
}
