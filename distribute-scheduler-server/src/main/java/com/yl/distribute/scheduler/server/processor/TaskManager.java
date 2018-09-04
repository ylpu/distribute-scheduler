package com.yl.distribute.scheduler.server.processor;

import java.util.HashMap;
import java.util.Map;
import com.yl.distribute.scheduler.common.bean.TaskRequest;

/**
 * 任务管理
 *
 */
public class TaskManager {    
    
    private static Map<String,TaskRequest> taskMap = new HashMap<String,TaskRequest>();
    

    public static void addTask(TaskRequest task) {
        taskMap.put(task.getId(), task);
    }

    public static void removeTask(String id) {
        taskMap.remove(id);
    }
    
    public static Map<String, TaskRequest> getTaskMap() {
        return taskMap;
    }

    public static void setTaskMap(Map<String, TaskRequest> taskMap) {
        TaskManager.taskMap = taskMap;
    }   
}
