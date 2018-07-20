package com.yl.distribute.scheduler.client.test;

import java.util.Map;
import java.util.Map.Entry;
import com.yl.distribute.scheduler.client.JobClient;
import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.core.service.ResourceService;

class ProduceThread extends Thread{
    
    private JobClient client;
    private int index;
    
    public ProduceThread(JobClient client,int index) {
        this.client = client;
        this.index = index;
    }
    
    public void run() {
        JobRequest input = new JobRequest();
        input.setRequestId(String.valueOf(Math.random() + index));
        input.setCommand("ls -ltr");
        input.setPoolName("pool1");            
        client.submit(input);
    }
}
public class ClientTest {
    
    public static void main(String[] args) throws Exception {
//        ResourceManager.getInstance().init();
        JobClient client = JobClient.getInstance();
        for(int i = 0;i < 50; i++) {
            new ProduceThread(client,i).start();
        }
        Thread.sleep(10000);
        ResourceService service = ResourceProxy.get(ResourceService.class);
        Map<String,HostInfo> map = service.getResources();
        System.out.println("map size is " + map.size());
        for(Entry<String,HostInfo> entry : map.entrySet()) {
            System.out.println("host is " + entry.getKey() +  ",availiable cores is " + entry.getValue().getCores() 
                    +  ",availiable memory is " + entry.getValue().getMemory()); 
        }        
    }
}