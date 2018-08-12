package com.yl.distribute.scheduler.web.service;

import org.springframework.stereotype.Component;

import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.web.dao.TaskRepository;

@Component
public class TaskServiceImpl implements TaskService{
    
    public void updateTask(TaskRequest task) {
        TaskRepository.getInstance().updateTask(task);
    }
    
    public void insertTask(TaskRequest task) {
        TaskRepository.getInstance().addTask(task);
    }
    
    public TaskRequest getTaskById(String id) {
        return TaskRepository.getInstance().getTask(id);
    }
    
    public String getErrorLog(String id) {
        return TaskRepository.getInstance().getTask(id).getErrorOutputUrl();
    }
    
    public String getOutputLog(String id) {
        return TaskRepository.getInstance().getTask(id).getStdOutputUrl();
    }
}
