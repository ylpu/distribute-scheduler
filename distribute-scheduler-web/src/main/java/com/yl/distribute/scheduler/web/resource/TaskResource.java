package com.yl.distribute.scheduler.web.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.web.service.TaskService;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
/**
 * JAX-RS resource class that provides operations for jobs.
 *
 */

@Path("task")
@Scope("singleton")
public class TaskResource {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private TaskService taskService;

    @GET
    @Path("getAllTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks() { 
        return Response.ok().build();
    }
    
    @POST
    @Path("addTask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTask(TaskRequest task) {
    	taskService.insertTask(task);
        return Response.ok().build();
    }
    
    @PUT
    @Path("updateTask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTask(TaskRequest task) {
        taskService.updateTask(task);
        return Response.ok().build();
    }
    
    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaskById(@PathParam("id") String id) {
       return Response.ok(taskService.getTaskById(id)).build();
    }
    
   
    @GET
    @Path("getErrorLog/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getErrorLog(@PathParam("id") String id) {
        return Response.ok(taskService.getErrorLog(id)).build();
    }
    
    @GET
    @Path("getOutputLog/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutputLog(@PathParam("id") String id) {
        return Response.ok(taskService.getOutputLog(id)).build();
    }
}