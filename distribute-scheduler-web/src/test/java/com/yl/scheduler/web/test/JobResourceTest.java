package com.yl.scheduler.web.test;

import java.util.Date;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;

/**
 * Tests for the user resource class.
 *
 */
public class JobResourceTest{

//    @Test
    public void getTask() {
        TaskRequest task = JerseyClient.get("http://localhost:8085/api/task/1", TaskRequest.class);
        System.out.println(task);
    }
    
    @Test
    public void addTask() {
    	String id = "1";
        JobConf jobConf = new JobConf();
        TaskRequest task = new TaskRequest();
        jobConf.setJobId(id);
        jobConf.setCommand("ls -ltr");
        jobConf.setPoolPath("/root/pool1");
        task.setJob(jobConf);
        task.setTaskId(id);
        task.setStartTime(new Date()); 
        task.setTaskStatus(TaskStatus.SUBMIT);
        Response response = JerseyClient.add("http://localhost:8085/api/task/addTask", task);
        System.out.println(response);
    }
    
    @Test
    public void updateTask() {
        TaskResponse task = new TaskResponse();
        task.setTaskId("1");
        task.setTaskStatus(TaskStatus.FAILED);
        Response response = JerseyClient.update("http://localhost:8085/api/task/updateTask", task);
        System.out.println(response);
    }
}