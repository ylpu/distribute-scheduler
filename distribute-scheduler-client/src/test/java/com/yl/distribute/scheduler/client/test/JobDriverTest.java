package com.yl.distribute.scheduler.client.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.yl.distribute.scheduler.client.callback.TaskResponseCallBack;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class JobDriverTest {
    
    private static Stack<JobConf> stack = new Stack<JobConf>();
    private static Set<JobConf> visited = new HashSet<JobConf>();
    
    /**
     * 解析任务并交给TaskClient去提交
     * @param rootTask
     * @throws Exception
     */    
    public static void parseJob(JobConf job) {
        if(job == null ) {
            return;
        } 
        if(job.getJobReleation().getParentJobs() == null) {
            System.out.println("submit task for job " + job.getCommand());
        }
        if(job.getJobReleation().getChildJobs() != null){
            List<JobConf> childs = job.getJobReleation().getChildJobs();
            for(JobConf jobConf : childs) {
                parseJob(jobConf);
                if(!visited.contains(jobConf)) {
                    if(!parentsFinished(jobConf)){
                        stack.push(jobConf);
                    }
                    else{
                        System.out.println("submit task for job " + jobConf.getCommand());   
                    }
                }      
                visited.add(jobConf);
            }   
        }
    }
    
    
    private static boolean parentsFinished(JobConf job){
        if(job.getJobReleation().getParentJobs() == null){
            return true;
        }
        for(JobConf jobConf : job.getJobReleation().getParentJobs()){
            if(TaskResponseCallBack.get(jobConf.getJobId()) == null){
                return false;
            }
            if(!(TaskResponseCallBack.get(jobConf.getJobId()).getTaskStatus() == TaskStatus.FAILED ||
                    TaskResponseCallBack.get(jobConf.getJobId()).getTaskStatus() == TaskStatus.SUCCESS)){
                return false;
            }
        }
        return true;
    }    
 
    private static class JobCHecker extends Thread{
        public void run() {
            int submitTasks = 0;
            
            while(submitTasks < visited.size()){
                if(!stack.empty()){
                    JobConf job = stack.pop();
                    if (parentsFinished(job)){
                        submitTasks += 1;
                        System.out.println("submit task for job " + job.getCommand());
                    }else{
                        stack.push(job);
                    }
                }    
            }
            for(JobConf job : visited) {
                TaskResponseCallBack.remove(job.getJobId());
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException{        
        
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
        job2.getJobReleation().setChildJobs(null);         

        job3.getJobReleation().setParentJobs(Arrays.asList(job1));
        job3.getJobReleation().setChildJobs(null);  
       
        parseJob(job);  
        
        new JobCHecker().start();
        
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