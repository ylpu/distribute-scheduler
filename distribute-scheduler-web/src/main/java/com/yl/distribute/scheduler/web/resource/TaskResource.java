package com.yl.distribute.scheduler.web.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.yl.distribute.scheduler.common.bean.Task;
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
    public Response addTask(Task task) {
    	taskService.insertTask(task);
        return Response.ok().build();
    }
    
    @PUT
    @Path("updateTask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTask(Task task) {
        taskService.updateTask(task);
        return Response.ok().build();
    }
    
    @GET
    @Path("{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaskById(@PathParam("taskId") String taskId) {
       return Response.ok(taskService.getTaskById(taskId)).build();
    }
    
   
    @GET
    @Path("getErrorLog/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getErrorLog(@PathParam("taskId") String taskId) {
        return Response.ok(taskService.getErrorLog(taskId)).build();
    }
    
    @GET
    @Path("getOutputLog/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutputLog(@PathParam("taskId") String taskId) {
        return Response.ok(taskService.getOutputLog(taskId)).build();
    }
}