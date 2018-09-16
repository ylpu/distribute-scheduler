package com.yl.distribute.scheduler.core.task;

import java.util.Date;
import java.util.Properties;
import javax.ws.rs.core.Response;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;

public class TaskManager {
    
    private static final TaskManager taskManager = new TaskManager();
    private TaskManager(){
        
    }
    
    public static TaskManager getInstance(){
        return taskManager;
    }
    
    public void updateTask(TaskRequest task,TaskStatus taskStatus){
        long elapseTime = (System.currentTimeMillis() - task.getStartTime().getTime())/1000;
        task.setTaskStatus(taskStatus);
        task.setEndTime(new Date());
        task.setElapseTime(elapseTime);
        updateTask(task);
    }
    
    public Response updateTask(TaskRequest task) {
        Properties prop = Configuration.getConfig("config.properties");        
        String taskApi = Configuration.getString(prop, "task.web.api");
        return JerseyClient.update(taskApi + "/" + "updateTask", task);
    }
    
    public Response addTask(TaskRequest task) {
        Properties prop = Configuration.getConfig("config.properties");        
        String taskApi = Configuration.getString(prop, "task.web.api");
        return JerseyClient.add(taskApi + "/" + "addTask", task);
    }
}
