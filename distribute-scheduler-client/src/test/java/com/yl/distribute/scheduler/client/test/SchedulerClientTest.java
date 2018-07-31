package com.yl.distribute.scheduler.client.test;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import org.junit.Test;
import com.yl.distribute.scheduler.client.JobClient;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.enums.JobType;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.core.resource.rpc.ResourceProxy;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;

public class SchedulerClientTest {
    
    @Test
    public void submitJob() throws Exception {
        
        JobClient client = JobClient.getInstance();        
        for(int i = 0;i < 10; i++) {
            new ProduceThread(client,i).start();
        }        
        Thread.sleep(10000);        
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
            TaskRequest task = new TaskRequest();
            jobConf.setJobId(id);
            jobConf.setJobType(JobType.COMMAND);
            jobConf.setCommand("ls -ltr");
            jobConf.setPoolPath("/root/pool1");
            task.setJob(jobConf);
            task.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            task.setTaskId(id);
            task.setStartTime(new Date()); 
            task.setTaskStatus(TaskStatus.SUBMIT);
            client.submit(task);
        }
    }
 }