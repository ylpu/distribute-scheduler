package com.yl.distribute.scheduler.web.task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.yl.distribute.scheduler.common.bean.TaskRequest;

public class TaskManager {
    
    private Map<String,TaskRequest> taskMap = new ConcurrentHashMap<String,TaskRequest>();
    private static TaskManager taskManager = new TaskManager();
    
    private TaskManager() {        
    }
    
    public static TaskManager getInstance() {
        return taskManager;
    }    
    
    public void addTask(TaskRequest task) {
    	taskMap.put(task.getId(), task);
    }
    
    public void removeTask(String id) {
    	taskMap.remove(id);
    }
    
    public void updateTask(TaskRequest task) {
       if(task == null || StringUtils.isEmpty(task.getId())){
    	   throw new RuntimeException("task can not empty");
       }
       TaskRequest newTask = taskMap.get(task.getId());
       if(task.getEndTime() != null){
    	   newTask.setEndTime(task.getEndTime());
       }
       if(StringUtils.isNotBlank(task.getErrorOutputUrl())){
    	   newTask.setErrorOutputUrl(task.getErrorOutputUrl());
       }       
       if (StringUtils.isNotBlank(task.getRunningServer())){
    	   newTask.setRunningServer(task.getRunningServer());
       }
       if(task.getStartTime() != null){
    	   newTask.setStartTime(task.getStartTime());
       }
       if(StringUtils.isNotBlank(task.getStdOutputUrl())){
    	   newTask.setStdOutputUrl(task.getStdOutputUrl());    	   
       }
       if(task.getTaskStatus() != null){
    	   newTask.setTaskStatus(task.getTaskStatus());
       }
       newTask.setFailedTimes(task.getFailedTimes());
    }
    
    public TaskRequest getTask(String id) {
        return taskMap.get(id);
    }
}