package com.yl.distribute.scheduler.client.schedule;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.client.TaskClient;
import com.yl.distribute.scheduler.client.callback.TaskResponseManager;
import com.yl.distribute.scheduler.common.bean.JobRequest;
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
    
    private Long JOB_CHECK_INTERVAL = 100l;
    
    private Stack<JobRequest> stack = new Stack<JobRequest>();
    private static Set<String> visited = new HashSet<String>();
    
    private Queue<JobRequest> queue = new LinkedList<JobRequest>();
    private Queue<JobRequest> bfsQueue = new LinkedList<JobRequest>();
    private static Set<String> bfsVisited = new HashSet<String>();
    
    private JobRequest jobRequest;
    
    public JobDriver(JobRequest jobRequest){
        this.jobRequest = jobRequest;
    }
    
    public void start() {
//        startJob(job);
//        new Thread(new DFSJobChecker()).start();
        bfsVisit(jobRequest);
        new Thread(new BFSJobChecker()).start();
        
    }
    
    /**
     * 解析任务并交给TaskClient去提交
     * @param rootTask
     * @throws Exception
     */ 
    @Deprecated
    public void startJob(JobRequest job) {
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
        List<JobRequest> childrenJob = job.getJobReleation().getChildJobs();
        if(childrenJob != null){
            for(JobRequest jobConf : childrenJob) {
                startJob(jobConf);
                if(!visited.contains(jobConf.getJobId())) {
                    stack.push(jobConf);
                    visited.add(jobConf.getJobId());
                }                
            }   
        }
    }   
    
    
    public void bfsVisit(JobRequest job) {
        queue.offer(job);
        bfsVisited.add(job.getJobId());
        bfsLoop();
    }

    private void bfsLoop() {
        JobRequest currentJob = queue.poll(); //出队
        bfsQueue.offer(currentJob);
        List<JobRequest> childrenJob = currentJob.getJobReleation().getChildJobs();
        if(childrenJob != null && childrenJob.size() > 0){
            for (JobRequest job : childrenJob) {
                if(!bfsVisited.contains(job.getJobId())) {
                    bfsVisited.add(job.getJobId());
                    queue.offer(job);
                }            
            }  
        }
        if (!queue.isEmpty()) {  //如果队列不为空继续遍历
            bfsLoop();
        }
    }
    
    private boolean parentJobsHaveFinished(JobRequest job){
        if(job.getJobReleation().getParentJobs() == null){
            return true;
        }
        //父任务中只要有一个没有完成就退出
        for(JobRequest jobConf : job.getJobReleation().getParentJobs()){
            if(!jobHasFinished(jobConf)) {
            	return false;
            }
        }
        return true;
    }
    
    
    private boolean jobHasFinished(JobRequest job){
        TaskResponse taskResponse = TaskResponseManager.get(job.getJobId());
        if(taskResponse != null){            
            if(taskResponse.getTaskStatus() == TaskStatus.SUCCESS){
                return true;
            }
        }
        return false;
    }
    
    private void submitJob(JobRequest job) {
        TaskRequest taskRequest = initTask(jobRequest);
        JobRequest jobConf = getJobDetail(jobRequest.getJobId());
        if(jobConf == null){
            throw new RuntimeException("can not get job for jobId " + jobRequest.getJobId());
        }
        if(StringUtils.isNotBlank(jobConf.getCronExpression())){
            JobScheduleInfo scheduleInfo = new JobScheduleInfo();
            setScheduleInfo(scheduleInfo,taskRequest);
            JobScheduler.addJob(scheduleInfo, UserJob.class);
        }else{
            TaskClient client = TaskClient.getInstance();
            taskRequest.setTaskStatus(TaskStatus.SUBMIT);
            client.submit(taskRequest);
        }        
    }
    
    private JobRequest getJobDetail(String jobId) {
        Properties prop = Configuration.getConfig("config.properties");        
        String jobApi = Configuration.getString(prop, "job.web.api");
        return JerseyClient.get(jobApi + "/getJobById/" + jobId, JobRequest.class);
    }
    
    private void setScheduleInfo(JobScheduleInfo scheduleInfo,TaskRequest taskRequest){
        scheduleInfo.setJobName(taskRequest.getJob().getJobId() + "-" + taskRequest.getTaskId());
        scheduleInfo.setJobGroupName(taskRequest.getJob().getJobName() + "_group");
        scheduleInfo.setTriggerName(taskRequest.getJob().getJobId() + "-" + taskRequest.getTaskId() + "_trigger");
        scheduleInfo.setTriggerGroupName(taskRequest.getJob().getJobName() + "_triggerGroup");
        scheduleInfo.setData(taskRequest);
    }
    
    private TaskRequest initTask(JobRequest job) {
        TaskRequest task = new TaskRequest();
        task.setTaskId(new ObjectId().toHexString());
        task.setJob(job);
        task.setStartTime(new Date());
        task.setEndTime(null);
        task.setLastFailedHost("");
        task.setRunningHost("");
        task.setFailedTimes(0);
        task.setStdOutputUrl("");
        task.setErrorOutputUrl("");
        return task;
    }

    @Deprecated
    private final class DFSJobChecker implements Runnable{
    	JobRequest job = null;
    	Properties prop = Configuration.getConfig("config.properties");
    	Long jobCheckInterval = prop.get("job.check.interval") == null ? 
    			JOB_CHECK_INTERVAL : Configuration.getLong(prop, "job.check.interval");
        public void run() {
            //任务是否遍历完
            while(!stack.isEmpty()){
                job = stack.peek();
                //如果父任务执行完成就提交当前任务，如果没有完成压入栈顶，每隔1秒检查父任务是否完成
                if(job != null){
                    if (parentJobsHaveFinished(job)){
                        if(!jobHasFinished(job)){
                            System.out.println("submit job " + job.getJobId());
                            LOG.info("submit job " + job.getJobId());
                            submitJob(job);
                        }
                        stack.pop();
                    }   
                }
 
                try {
                    Thread.sleep(jobCheckInterval);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            }  
            //最后一个任务完成后清理callback
            while(!jobHasFinished(job)) {
                try {
                    Thread.sleep(jobCheckInterval);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            }
            for(String jobId : visited) {
                System.out.println("remove job " + jobId);
                TaskResponseManager.remove(jobId);
            }
        }        
    }
    private final class BFSJobChecker implements Runnable{

        public void run() {
        	JobRequest job = null;
        	Properties prop = Configuration.getConfig("config.properties");
        	Long jobCheckInterval = prop.get("job.check.interval") == null ? 
        			JOB_CHECK_INTERVAL : Configuration.getLong(prop, "job.check.interval");
        	try {
                //任务是否遍历完
                while(!bfsQueue.isEmpty()){
                    job = bfsQueue.peek();
                    //如果父任务执行完成就提交当前任务，如果没有完成压入队列顶，每隔1秒检查父任务是否完成
                    if(job != null){
                        if (parentJobsHaveFinished(job)){
                            if(!jobHasFinished(job)){
                                System.out.println("submit job " + job.getJobId());
                                LOG.info("submit job " + job.getJobId());
                                submitJob(job);
                            }
                            bfsQueue.poll();
                        }   
                    }     
                    try {
                        Thread.sleep(jobCheckInterval);
                    } catch (InterruptedException e) {
                        LOG.error(e);
                    }
                } 
        	}finally {
        		//最后一个任务完成后清理callback
        		while(!jobHasFinished(job)){
        			try {
						Thread.sleep(jobCheckInterval);
					} catch (InterruptedException e) {
						LOG.error(e);
					}
        		}  
        		cleanCallBack();
        	}            
        }   
        
        private void cleanCallBack() {
            //所有任务执行完清除callback
            for(String jobId : bfsVisited) {
                System.out.println("remove job " + jobId);
                TaskResponseManager.remove(jobId);
            }
        }
    }
}
