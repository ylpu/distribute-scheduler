package com.yl.distribute.scheduler.web.service;

import com.yl.distribute.scheduler.common.bean.Task;

public interface TaskService {
	
    public void updateTask(Task task);
    
    public void insertTask(Task task);
    
    public Task getTaskById(String taskId);
    
    public String getErrorLog(String taskId);
    
    public String getOutputLog(String taskId);
}
