package com.yl.distribute.scheduler.client.test;

import java.io.File;
import org.dom4j.Element;
import org.junit.Test;
import com.yl.distribute.scheduler.client.callback.TaskResponseManager;
import com.yl.distribute.scheduler.client.job.JobDriver;
import com.yl.distribute.scheduler.client.job.JobParser;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class JobDriverTest {
    
    @Test
    public void jobDriverTest() throws InterruptedException{        
        
        TaskResponse tr = new TaskResponse();
        tr.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseManager.add("a", tr);
        
        TaskResponse tr1 = new TaskResponse();
        tr1.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseManager.add("b", tr1);
        
        TaskResponse tr2 = new TaskResponse();
        tr2.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseManager.add("c", tr2);
        
        TaskResponse tr3 = new TaskResponse();
        tr3.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseManager.add("d", tr3); 
        
        File file = new File("src/main/resources/jobplan.xml");
        JobParser parser = new JobParser(file);
        Element element = parser.readFile();
        JobConf rootJob = parser.getRootJob(element);
        
        new JobDriver(rootJob).start();
        
        //模拟5秒a任务执行完成,开始执行b,c
        Thread.sleep(5000);
        
        TaskResponse tr4 = new TaskResponse();
        tr4.setTaskStatus(TaskStatus.SUCCESS);        
        TaskResponseManager.add("a", tr4);
        
        //模拟10秒b,c任务执行完成,开始执行d任务
        Thread.sleep(10000);
        
        TaskResponse tr5 = new TaskResponse();
        tr5.setTaskStatus(TaskStatus.SUCCESS);        
        TaskResponseManager.add("b", tr5);  
        
        TaskResponse tr6 = new TaskResponse();
        tr6.setTaskStatus(TaskStatus.SUCCESS);        
        TaskResponseManager.add("c", tr6);
        
        Thread.sleep(2000);
    }
}