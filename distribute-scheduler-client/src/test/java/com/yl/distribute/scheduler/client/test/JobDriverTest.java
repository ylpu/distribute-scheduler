package com.yl.distribute.scheduler.client.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import com.yl.distribute.scheduler.client.callback.TaskResponseCallBack;
import com.yl.distribute.scheduler.client.driver.JobDriver;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class JobDriverTest {   
    
    @Test
    public void dependencyCycle() {
        TaskResponse tr = new TaskResponse();
        tr.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseCallBack.add("a", tr);
        
        TaskResponse tr1 = new TaskResponse();
        tr1.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseCallBack.add("b", tr1);
        
        TaskResponse tr2 = new TaskResponse();
        tr2.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseCallBack.add("c", tr2);
        
        TaskResponse tr3 = new TaskResponse();
        tr3.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseCallBack.add("d", tr3);
        
        
        List<JobConf> childs = new ArrayList<JobConf>();
        
        JobConf job = new JobConf();
        job.setJobId("a");
        job.setCommand("execute a");
        
        JobConf job1 = new JobConf();
        job1.setJobId("b");
        job1.setCommand("execute b");
        
        JobConf job2 = new JobConf();
        job2.setJobId("c");
        job2.setCommand("execute c");
        
        JobConf job3 = new JobConf();
        job3.setJobId("d");
        job3.setCommand("execute d");
        
        childs.add(job1);
        childs.add(job2);
        job.getJobReleation().setParentJobs(null);
        job.getJobReleation().setChildJobs(childs);
        
        job1.getJobReleation().setParentJobs(Arrays.asList(job));
        job1.getJobReleation().setChildJobs(Arrays.asList(job3));       
        
        job2.getJobReleation().setParentJobs(Arrays.asList(job));
        job2.getJobReleation().setChildJobs(Arrays.asList(job3));         

        job3.getJobReleation().setParentJobs(Arrays.asList(job1,job2));
        job3.getJobReleation().setChildJobs(null); 
        
        JobDriver jobDriver = new JobDriver(job);        
        
        System.out.println("has cycle dependency " + jobDriver.detectCycle(job));      

    }
    
    @Test
    public void jobSubmit() throws InterruptedException {
        TaskResponse tr = new TaskResponse();
        tr.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseCallBack.add("a", tr);
        
        TaskResponse tr1 = new TaskResponse();
        tr1.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseCallBack.add("b", tr1);
        
        TaskResponse tr2 = new TaskResponse();
        tr2.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseCallBack.add("c", tr2);
        
        TaskResponse tr3 = new TaskResponse();
        tr3.setTaskStatus(TaskStatus.SUBMIT);        
        TaskResponseCallBack.add("d", tr3);        
        
        List<JobConf> rootChilds = new ArrayList<JobConf>();
        
        JobConf job = new JobConf();
        job.setJobId("a");
        job.setCommand("execute a");
        
        JobConf job1 = new JobConf();
        job1.setJobId("b");
        job1.setCommand("execute b");
        
        JobConf job2 = new JobConf();
        job2.setJobId("c");
        job2.setCommand("execute c");
        
        JobConf job3 = new JobConf();
        job3.setJobId("d");
        job3.setCommand("execute d");
        
        rootChilds.add(job1);
        rootChilds.add(job2);
        job.getJobReleation().setParentJobs(null);
        job.getJobReleation().setChildJobs(rootChilds);
        
        job1.getJobReleation().setParentJobs(Arrays.asList(job));
        job1.getJobReleation().setChildJobs(Arrays.asList(job3));       
        
        job2.getJobReleation().setParentJobs(Arrays.asList(job));
        job2.getJobReleation().setChildJobs(Arrays.asList(job3));         

        job3.getJobReleation().setParentJobs(Arrays.asList(job1,job2));
        job3.getJobReleation().setChildJobs(null); 
        
        JobDriver jobDriver = new JobDriver(job);        
        
        jobDriver.start();
        
        //模拟5秒a任务执行完成,开始执行b,c
        Thread.sleep(5000);
        
        TaskResponse tr4 = new TaskResponse();
        tr4.setTaskStatus(TaskStatus.SUCCESS);        
        TaskResponseCallBack.add("a", tr4);
        
        //模拟10秒b,c任务执行完成,开始执行d任务
        Thread.sleep(10000);
        
        TaskResponse tr5 = new TaskResponse();
        tr5.setTaskStatus(TaskStatus.SUCCESS);        
        TaskResponseCallBack.add("b", tr5);  
        
        TaskResponse tr6 = new TaskResponse();
        tr6.setTaskStatus(TaskStatus.SUCCESS);        
        TaskResponseCallBack.add("c", tr6);
    }
 }