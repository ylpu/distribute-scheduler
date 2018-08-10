package com.yl.distribute.scheduler.client.job;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yl.distribute.scheduler.client.TaskClient;
import com.yl.distribute.scheduler.client.callback.TaskResponseCallBack;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.enums.TaskStatus;

public class JobDriver {
    
    private Log LOG = LogFactory.getLog(JobDriver.class);
    
    private Long JOB_CHECK_INTERVAL = 1000L;
    
    private Stack<JobConf> stack = new Stack<JobConf>();
    private static Set<String> visited = new HashSet<String>();
    
    private JobConf job;
    
    public JobDriver(JobConf job){
        this.job = job;
    }
    
    public void start() {
        parseJob(job);
        new Thread(new JobChecker()).start();
    }
    
    /**
     * 解析任务并交给TaskClient去提交
     * @param rootTask
     * @throws Exception
     */    
    public void parseJob(JobConf job) {
        if(job == null ) {
            return;
        } 
        if(job.getJobReleation().getParentJobs() == null) {
            System.out.println("submit job " + job.getJobId());
            LOG.info("submit job " + job.getJobId());
            submitJob(job);
        }
        if(job.getJobReleation().getChildJobs() != null){
            List<JobConf> childs = job.getJobReleation().getChildJobs();
            for(JobConf jobConf : childs) {
                parseJob(jobConf);
                //防止重复提交,如a->b,c->d,不检查的话d会被提交两次
                if(!visited.contains(jobConf.getJobId())) {
                    //如果父任务没有完成，就推到栈顶，如果完成了就去提交
                    if(!parentsJobFinished(jobConf)){
                        stack.push(jobConf);
                    }
                    else{
                        System.out.println("submit job " + jobConf.getJobId());
                        LOG.info("submit job " + jobConf.getJobId());
                        submitJob(job);
                    }
                }      
                visited.add(jobConf.getJobId());
            }   
        }
    }
    
    
    private boolean parentsJobFinished(JobConf job){
        if(job.getJobReleation().getParentJobs() == null){
            return true;
        }
        //父任务中只要有一个没有完成就退出
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
    
    private void submitJob(JobConf job) {
        TaskClient client = TaskClient.getInstance();
        TaskRequest task = new TaskRequest();
        String taskId = String.valueOf(Math.random());
        task.setJob(job);
        task.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        task.setTaskId(taskId);
        task.setStartTime(new Date()); 
        task.setTaskStatus(TaskStatus.SUBMIT);
        client.submit(task);
    }
    
    private final class JobChecker implements Runnable{

        public void run() {
            int submittedJobs = 0;
            //任务是否遍历完
            while(submittedJobs < visited.size()){
                if(!stack.empty()){
                    JobConf job = stack.pop();
                    //如果父任务执行完成就提交当前任务，如果没有完成压入栈顶，每隔1秒检查父任务是否完成
                    if (parentsJobFinished(job)){
                        System.out.println("submit job " + job.getJobId());
                        LOG.info("submit job " + job.getJobId());
                        submitJob(job);
                        submittedJobs += 1;
                    }else{
                        stack.push(job);
                        try {
                            Thread.sleep(JOB_CHECK_INTERVAL);
                        } catch (InterruptedException e) {
                            LOG.error(e);
                        }
                    }
                }    
            }  
            //所有任务执行完清除callback
            for(String jobId : visited) {
                TaskResponseCallBack.remove(jobId);
            }
        }        
    }
}
