package com.yl.distribute.scheduler.client.test;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.yl.distribute.scheduler.client.JobClient;
import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.Task;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import com.yl.distribute.scheduler.core.service.ResourceService;

public class SchedulerClientTest {
    
    @Test
    public void submitJob() throws Exception {
        
        JobClient client = JobClient.getInstance();        
        for(int i = 0;i < 500; i++) {
            new ProduceThread(client,i).start();
        }        
        Thread.sleep(180000);        
        ResourceService service = ResourceProxy.get(ResourceService.class);
        Map<String,HostInfo> map = service.getResources();
        for(Entry<String,HostInfo> entry : map.entrySet()) {
            System.out.println("host is " + entry.getKey() +  ",availiable cores is " + entry.getValue().getAvailableCores() 
                    +  ",availiable memory is " + entry.getValue().getAvailableMemory()); 
        } 
        
        Map<String,Integer> tasks = service.getTasks();
        for(Entry<String,Integer> entry : tasks.entrySet()) {
            System.out.println("host is " + entry.getKey() +  ",task number is " + entry.getValue()); 
        } 
    }
    
    public static class ProduceThread extends Thread{
        
        private JobClient client;
        private int index;
        
        public ProduceThread(JobClient client,int index) {
            this.client = client;
            this.index = index;
        }
        
        public void run() {
        	String id = String.valueOf(Math.random() + index);
            JobConf jobConf = new JobConf();
            Task task = new Task();
            jobConf.setJobId(id);
            jobConf.setCommand("ls -ltr");
            jobConf.setPoolPath("/root/pool1");
            task.setJob(jobConf);
            task.setTaskId(id);
            task.setStartTime(new Date()); 
            task.setTaskStatus(TaskStatus.INITIAL.getStatus());
            addTask(task);
            client.submit(task);
        }    
        
        private Response addTask(Task task) {
            Properties prop = Configuration.getConfig("config.properties");        
            String taskApi = Configuration.getString(prop, "task.web.api");
            return JerseyClient.add(taskApi + "/" + "addTask", task);
        }
    }
 }