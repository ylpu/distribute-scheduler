package com.yl.distribute.scheduler.web.service;

import org.springframework.stereotype.Component;

import com.yl.distribute.scheduler.common.bean.Task;
import com.yl.distribute.scheduler.web.task.TaskManager;

@Component
public class TaskServiceImpl implements TaskService{
    
    public void updateTask(Task task) {
        TaskManager.getInstance().updateTask(task);
    }
    
    public void insertTask(Task task) {
        TaskManager.getInstance().addTask(task);
    }
    
    public Task getTaskById(String taskId) {
        return TaskManager.getInstance().getTask(taskId);
    }
    
    public String getErrorLog(String taskId) {
        return TaskManager.getInstance().getTask(taskId).getErrorOutputUrl();
    }
    
    public String getOutputLog(String taskId) {
        return TaskManager.getInstance().getTask(taskId).getStdOutputUrl();
    }
}
