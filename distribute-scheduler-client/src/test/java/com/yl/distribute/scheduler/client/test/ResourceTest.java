package com.yl.distribute.scheduler.client.test;

import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.core.service.ResourceService;

public class ResourceTest {
    
   public static void main(String[] args) {
       ResourceService service = ResourceProxy.get(ResourceService.class);
       JobRequest input = new JobRequest();
       input.setRequestId(String.valueOf(Math.random()));
       input.setCommand("ls -ltr");
       input.setPoolName("pool1");  
       System.out.println(service.getIdleServer(input, ""));
   }
}
