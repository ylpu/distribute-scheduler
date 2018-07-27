package com.yl.distribute.scheduler.client.callback;

import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yl.distribute.scheduler.client.JobClient;
import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.common.enums.JobStatus;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import com.yl.distribute.scheduler.core.service.ResourceService;

public class ClientCallback{
	
    private static Log LOG = LogFactory.getLog(ClientCallback.class);
    
    private JobRequest input;
    
    public ClientCallback(JobRequest input) {
        this.input = input;
    }
    
    public void onRead(JobResponse response) throws Exception {
    	System.out.println(input.getJobId() + "返回url是" + response.getStdOutputUrl());
    	LOG.info(input.getJobId() + "返回url是" + response.getStdOutputUrl());
        updateJob(response);
        ResourceService service = ResourceProxy.get(ResourceService.class);
        service.addResource(response.getRunningServer(), input.getExecuteParameters());
        service.decreaseTask(response.getRunningServer());
        resubmitIfNeccesery(response);
    }
    
    private Response updateJob(JobResponse response) {
        Properties prop = Configuration.getConfig("config.properties");        
        String jobApi = Configuration.getString(prop, "job.web.api");
        return JerseyClient.update(jobApi + "/" + "updateJob", response);
    }
    
    private void resubmitIfNeccesery(JobResponse output) throws Exception {
        if(output.getJobStatus().equals(JobStatus.FAILED.getStatus())
                && input.getFailedTimes() < input.getRetryTimes()) { 
            input.setFailedTimes(input.getFailedTimes() + 1);
            LOG.warn("任务"+ input.getJobId() + "执行失败，现在进行第" + input.getFailedTimes() + "次重试");
            JobClient.getInstance().submit(input);            
        }
    }
}