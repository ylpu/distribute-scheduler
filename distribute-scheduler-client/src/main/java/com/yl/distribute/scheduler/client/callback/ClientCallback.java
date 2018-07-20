package com.yl.distribute.scheduler.client.callback;

import java.util.Properties;
import javax.ws.rs.core.Response;
import com.yl.distribute.scheduler.client.JobClient;
import com.yl.distribute.scheduler.client.proxy.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.common.enums.JobStatus;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import com.yl.distribute.scheduler.core.service.ResourceService;

public class ClientCallback{
    
    private JobRequest input;
    
    public ClientCallback(JobRequest input) {
        this.input = input;
    }
    public void onRead(JobResponse response) throws Exception {
        ResourceService service = ResourceProxy.get(ResourceService.class);
        updateJob(response);
        service.addResource(response.getRunningServer(), input.getExecuteParameters());
        resubmitIfNeccesery(response);
    }
    
    private void resubmitIfNeccesery(JobResponse output) throws Exception {
        if(output.getJobStatus().equals(JobStatus.FAILED.getStatus())
                && input.getRetryTimes() < 3) { 
            input.setRetryTimes(input.getRetryTimes() + 1);
            JobClient.getInstance().submit(input);            
        }
    }
    
    private Response updateJob(JobResponse response) {
        Properties prop = Configuration.getConfig("config.properties");        
        String jobApi = Configuration.getString(prop, "job.web.api");
        return JerseyClient.update(jobApi + "/" + "updateJob", response);
    }
}