package com.yl.distribute.scheduler.web.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.web.service.JobService;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
/**
 * JAX-RS resource class that provides operations for jobs.
 *
 */

@Path("job")
@Scope("singleton")
public class JobResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private JobService jobService;
    
    @POST
    @Path("addJob")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addJob(JobRequest job) {
        jobService.addJob(job);
        return Response.ok().build();
    }
    
    @GET
    @Path("getJobById/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobById(@PathParam("id") String id) {
       return Response.ok(jobService.getJobById(id)).build();
    }
}