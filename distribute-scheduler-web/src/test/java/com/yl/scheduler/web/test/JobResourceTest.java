package com.yl.scheduler.web.test;

import javax.ws.rs.core.Response;

import org.junit.Test;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;

/**
 * Tests for the user resource class.
 *
 */
public class JobResourceTest{

    @Test
    public void getJob() {
        JobResponse response = JerseyClient.get("http://localhost:8085/api/jobs/1", JobResponse.class);
        System.out.println(response);
    }
    
    @Test
    public void addJob() {
        JobResponse jobResponse = new JobResponse();
        Response response = JerseyClient.update("http://localhost:8085/api/jobs/updateJob", jobResponse);
        System.out.println(response);
    }
}