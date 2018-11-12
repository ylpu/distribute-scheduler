package com.yl.distribute.scheduler.service;

import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.service.BaseService;
import com.yl.distribute.scheduler.entity.SchedulerTask;

public interface TaskService extends BaseService<SchedulerTask,String>{
    
    public String getErrorLog(String id);
    
    public String getOutputLog(String id);

    public void addTask(TaskRequest task);

    public void updateTask(TaskRequest task);

    public TaskRequest getTaskById(String id);
}
