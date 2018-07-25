package com.yl.distribute.scheduler.web.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.web.service.JobService;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
/**
 * JAX-RS resource class that provides operations for jobs.
 *
 */

@Path("jobs")
@Scope("singleton")
public class JobResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private JobService jobService;

    @GET
    @Path("getAllJobs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllJobs() { 
        return Response.ok().build();
    }
    
    @PUT
    @Path("addJob")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addJob(JobResponse response) {
        jobService.insertJob(response);
        return Response.ok().build();
    }
    
    @PUT
    @Path("updateJob")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateJob(JobResponse response) {
        jobService.updateJob(response);
        return Response.ok().build();
    }
    
    @GET
    @Path("{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobById(@PathParam("jobId") String jobId) {
       return Response.ok(jobService.getJobById(jobId)).build();
    }
    
    @GET
    @Path("getLastFailedServer/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastFailedServer(@PathParam("jobId") String jobId) {
        return Response.ok(jobService.getLastFailedServer(jobId)).build();
    }
    
    @GET
    @Path("getErrorLog/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getErrorLog(@PathParam("jobId") String jobId) {
        return Response.ok(jobService.getErrorLog(jobId)).build();
    }
    
    @GET
    @Path("getOutputLog/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutputLog(@PathParam("jobId") String jobId) {
        return Response.ok(jobService.getOutputLog(jobId)).build();
    }
}