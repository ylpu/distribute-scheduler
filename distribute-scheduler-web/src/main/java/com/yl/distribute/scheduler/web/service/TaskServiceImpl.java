package com.yl.distribute.scheduler.web.service;

import org.springframework.stereotype.Component;

import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.web.task.TaskManager;

@Component
public class TaskServiceImpl implements TaskService{
    
    public void updateTask(TaskRequest task) {
        TaskManager.getInstance().updateTask(task);
    }
    
    public void insertTask(TaskRequest task) {
        TaskManager.getInstance().addTask(task);
    }
    
    public TaskRequest getTaskById(String id) {
        return TaskManager.getInstance().getTask(id);
    }
    
    public String getErrorLog(String id) {
        return TaskManager.getInstance().getTask(id).getErrorOutputUrl();
    }
    
    public String getOutputLog(String id) {
        return TaskManager.getInstance().getTask(id).getStdOutputUrl();
    }
}
