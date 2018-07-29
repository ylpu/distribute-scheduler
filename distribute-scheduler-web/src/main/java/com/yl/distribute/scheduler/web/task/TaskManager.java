package com.yl.distribute.scheduler.web.task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.yl.distribute.scheduler.common.bean.Task;

public class TaskManager {
    
    private Map<String,Task> taskMap = new ConcurrentHashMap<String,Task>();
    private static TaskManager taskManager = new TaskManager();
    
    private TaskManager() {        
    }
    
    public static TaskManager getInstance() {
        return taskManager;
    }    
    
    public void addTask(Task task) {
    	taskMap.put(task.getTaskId(), task);
    }
    
    public void removeTask(String taskId) {
    	taskMap.remove(taskId);
    }
    
    public void updateTask(Task task) {
       if(task == null || StringUtils.isEmpty(task.getTaskId())){
    	   throw new RuntimeException("task can not empty");
       }
       Task newTask = taskMap.get(task.getTaskId());
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
       if(StringUtils.isNotBlank(task.getTaskStatus())){
    	   newTask.setTaskStatus(task.getTaskStatus());
       }
       newTask.setFailedTimes(task.getFailedTimes());
    }
    
    public Task getTask(String taskId) {
        return taskMap.get(taskId);
    }
}