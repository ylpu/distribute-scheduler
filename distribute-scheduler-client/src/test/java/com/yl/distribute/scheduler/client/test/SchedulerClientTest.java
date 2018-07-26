package com.yl.distribute.scheduler.client.test;

import java.util.Map;
import java.util.Map.Entry;
import org.junit.Test;
import com.yl.distribute.scheduler.client.JobClient;
import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.core.service.ResourceService;

public class SchedulerClientTest {
    
    @Test
    public void submitJob() throws Exception {
        
        JobClient client = JobClient.getInstance();        
        for(int i = 0;i < 1000; i++) {
            new ProduceThread(client,i).start();
        }        
        Thread.sleep(300000);        
        ResourceService service = ResourceProxy.get(ResourceService.class);
        Map<String,HostInfo> map = service.getResources();
        for(Entry<String,HostInfo> entry : map.entrySet()) {
            System.out.println("host is " + entry.getKey() +  ",availiable cores is " + entry.getValue().getAvailableCores() 
                    +  ",availiable memory is " + entry.getValue().getAvailableMemory()); 
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
            JobRequest input = new JobRequest();
            input.setJobId(String.valueOf(Math.random() + index));
            input.setCommand("ls -ltr");
            input.setPoolPath("/root/pool1");            
            client.submit(input);
        }
    }
 }