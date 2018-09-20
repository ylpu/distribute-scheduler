package com.yl.distribute.scheduler.web.dao;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.yl.distribute.scheduler.common.bean.TaskRequest;

public class TaskRepository {
    
    private Map<String,TaskRequest> taskMap = new ConcurrentHashMap<String,TaskRequest>();
    private static TaskRepository taskManager = new TaskRepository();
    
    private TaskRepository() {        
    }
    
    public static TaskRepository getInstance() {
        return taskManager;
    }    
    
    public void addTask(TaskRequest task) {
    	taskMap.put(task.getTaskId(), task);
    }
    
    public void removeTask(String taskId) {
    	taskMap.remove(taskId);
    }
    
    public void updateTask(TaskRequest task) {
       if(task == null || StringUtils.isEmpty(task.getTaskId())){
    	   throw new RuntimeException("task can not empty");
       }
       TaskRequest newTask = taskMap.get(task.getTaskId());
       if(task.getEndTime() != null){
    	   newTask.setEndTime(task.getEndTime());
       }
       if(StringUtils.isNotBlank(task.getErrorOutputUrl())){
    	   newTask.setErrorOutputUrl(task.getErrorOutputUrl());
       }       
       if (StringUtils.isNotBlank(task.getRunningHost())){
    	   newTask.setRunningHost(task.getRunningHost());
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
    
    public TaskRequest getTask(String taskId) {
        return taskMap.get(taskId);
    }
}