package com.yl.distribute.scheduler.client.schedule;

import java.util.Date;
import java.util.Properties;
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
 * @author
 *
 */
public class JobSubmit {
    
    private Log LOG = LogFactory.getLog(JobSubmit.class);
        
    private JobRequest jobRequest;
    
    public JobSubmit(JobRequest jobRequest){
        this.jobRequest = jobRequest;
    }
    
    private boolean parentJobsHaveFinished(JobRequest job){
        if(job.getJobReleation().getParentJobs() == null){
            return true;
        }
        //父任务中只要有一个没有完成就退出
        for(JobRequest jobConf : job.getJobReleation().getParentJobs()){
            if(!jobHasFinished(jobConf.getJobId())) {
               return false;
            }
        }
        return true;
    }
    
    
    private boolean jobHasFinished(String jobId){
        TaskResponse taskResponse = TaskResponseManager.get(jobId);
        if(taskResponse != null){            
            if(taskResponse.getTaskStatus() == TaskStatus.SUCCESS){
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     */
    public void submitJob() {
    	    JobRequest jobConf = getJobDetail(jobRequest.getJobId());
        TaskRequest taskRequest = initTask(jobConf);
        if(jobConf == null){
        	    LOG.error("can not get job details for jobId " + jobRequest.getJobId());
            throw new RuntimeException("can not get job details for jobId " + jobRequest.getJobId());
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
    
    /**
     * 根据任务获取任务详情
     * @param jobId
     * @return
     */
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
}
