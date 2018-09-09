package com.yl.distribute.scheduler.client.schedule;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.client.TaskClient;
import com.yl.distribute.scheduler.client.callback.TaskResponseManager;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.JobScheduleInfo;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;

/**
 * 解析任务并提交
 * @author
 *
 */
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
        startJob(job);
        new Thread(new JobChecker()).start();
    }
    
    /**
     * 解析任务并交给TaskClient去提交
     * @param rootTask
     * @throws Exception
     */    
    public void startJob(JobConf job) {
        if(job == null ) {
            return;
        } 
        //root任务
        if(job.getJobReleation().getParentJobs() == null) {
            if(!jobHasFinished(job)){
                System.out.println("submit job " + job.getJobId());
                LOG.info("submit job " + job.getJobId());
                submitJob(job);
            }
            visited.add(job.getJobId());
        }
        if(job.getJobReleation().getChildJobs() != null){
            List<JobConf> childs = job.getJobReleation().getChildJobs();
            for(JobConf jobConf : childs) {
                startJob(jobConf);
                if(!visited.contains(jobConf.getJobId())) {
                    stack.push(jobConf);
                    visited.add(jobConf.getJobId());
                }                
            }   
        }
    }   
    
    private boolean parentJobsHaveFinished(JobConf job){
        if(job.getJobReleation().getParentJobs() == null){
            return true;
        }
        //父任务中只要有一个没有完成就退出
        for(JobConf jobConf : job.getJobReleation().getParentJobs()){
            TaskResponse taskResponse = TaskResponseManager.get(jobConf.getJobId());
            if(taskResponse == null){
                return false;
            }
            if(!((taskResponse.getTaskStatus() == TaskStatus.FAILED )
                    || (taskResponse.getTaskStatus() == TaskStatus.SUCCESS))){
                return false;
            }
        }
        return true;
    }
    
    
    private boolean jobHasFinished(JobConf job){
        TaskResponse taskResponse = TaskResponseManager.get(job.getJobId());
        if(taskResponse != null){            
            if((taskResponse.getTaskStatus() == TaskStatus.FAILED )
                    || (taskResponse.getTaskStatus() == TaskStatus.SUCCESS)){
                return true;
            }
        }
        return false;
    }
    
    private void submitJob(JobConf job) {
        JobConf jobConf = getJobDetail(job.getJobId());
        if(jobConf == null){
            throw new RuntimeException("can not get job for jobId " + job.getJobId());
        }
        if(StringUtils.isNotBlank(jobConf.getCronExpression())){
            JobScheduleInfo scheduleInfo = new JobScheduleInfo();
            setScheduleInfo(scheduleInfo,jobConf);
            JobScheduler.addJob(scheduleInfo, UserJob.class);
        }else{
            submitTask(jobConf); 
        }        
    }
    
    private JobConf getJobDetail(String jobId) {
        Properties prop = Configuration.getConfig("config.properties");        
        String jobApi = Configuration.getString(prop, "job.web.api");
        return JerseyClient.get(jobApi + "/getJobById/" + jobId, JobConf.class);
    }
    
    private void setScheduleInfo(JobScheduleInfo scheduleInfo,JobConf job){
        scheduleInfo.setJobName(job.getJobName());
        scheduleInfo.setJobGroupName(job.getJobName() + "_group");
        scheduleInfo.setTriggerName(job.getJobName() + "_trigger");
        scheduleInfo.setTriggerGroupName(job.getJobName() + "_triggerGroup");
        scheduleInfo.setData(job);
    }
    
    private void submitTask(JobConf job){
        TaskClient client = TaskClient.getInstance();
        TaskRequest task = new TaskRequest();
        task.setId(new ObjectId().toHexString());
        task.setTaskId(new ObjectId().toHexString());
        task.setJob(job);
        task.setStartTime(new Date());
        task.setEndTime(null);
        task.setLastFailedHost("");
        task.setRunningHost("");
        task.setFailedTimes(0);
        task.setStdOutputUrl("");
        task.setErrorOutputUrl("");
        task.setTaskStatus(TaskStatus.SUBMIT);
        client.submit(task);
    }
    
    private final class JobChecker implements Runnable{

        public void run() {
            int jobCount = 0;
            //任务是否遍历完
            while(jobCount < visited.size()-1){
                if(!stack.empty()){
                    JobConf job = stack.pop();
                    //如果父任务执行完成就提交当前任务，如果没有完成压入栈顶，每隔1秒检查父任务是否完成
                    if (parentJobsHaveFinished(job)){
                        if(!jobHasFinished(job)){
                            System.out.println("submit job " + job.getJobId());
                            LOG.info("submit job " + job.getJobId());
                            submitJob(job);
                        }
                        jobCount += 1;
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
                System.out.println("remove job " + jobId);
                TaskResponseManager.remove(jobId);
            }
        }        
    }
}
