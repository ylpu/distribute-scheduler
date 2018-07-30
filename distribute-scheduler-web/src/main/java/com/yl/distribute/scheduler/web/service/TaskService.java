package com.yl.distribute.scheduler.web.service;

import com.yl.distribute.scheduler.common.bean.TaskRequest;

public interface TaskService {
	
    public void updateTask(TaskRequest task);
    
    public void insertTask(TaskRequest task);
    
    public TaskRequest getTaskById(String id);
    
    public String getErrorLog(String id);
    
    public String getOutputLog(String id);
}
