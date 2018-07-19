package com.yl.distribute.scheduler.client.callback;

import java.util.Properties;
import javax.ws.rs.core.Response;
import com.yl.distribute.scheduler.client.JobClient;
import com.yl.distribute.scheduler.client.resource.ResourceManager;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.common.config.Configuration;
import com.yl.distribute.scheduler.common.enums.JobStatus;
import com.yl.distribute.scheduler.common.jersey.JerseyClient;

public class ClientCallback{
    
    private JobRequest input;
    
    public ClientCallback(JobRequest input) {
        this.input = input;
    }
    public void onRead(JobResponse response) throws Exception {
        updateJob(response);
        ResourceManager resource = ResourceManager.getInstance();
        resource.addResource(response.getRunningServer(), input.getExecuteParameters());
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