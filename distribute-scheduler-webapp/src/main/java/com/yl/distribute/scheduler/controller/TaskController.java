package com.yl.distribute.scheduler.controller;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.yl.distribute.scheduler.common.bean.SchedulerResponse;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.service.TaskService;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * JAX-RS resource class that provides operations for jobs.
 *
 */

@Controller
@RequestMapping("/task")
public class TaskController {
	
    @Autowired
    private TaskService taskService;

    @GET
    @Path("getAllTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks() { 
        return Response.ok().build();
    }
    
    @ResponseBody
    @RequestMapping(value="/addTask",method=RequestMethod.POST)
    public void addTask(@RequestBody TaskRequest task) {
    	taskService.addTask(task);

    }
    
    @ResponseBody
    @RequestMapping(value="/updateTask",method=RequestMethod.POST)
    public void updateTask(TaskRequest task) {
        taskService.updateTask(task);
    }    

    @ResponseBody
    @RequestMapping(value="/getTaskById/{id}",method=RequestMethod.GET)
    public SchedulerResponse<TaskRequest> getTaskById(@PathVariable String id) {
    	return new SchedulerResponse<TaskRequest>(taskService.getTaskById(id));
    }   
   

    @ResponseBody
    @RequestMapping(value="getErrorLog/{id}",method=RequestMethod.GET)
    public void getErrorLog(@PathVariable String id) {
        taskService.getErrorLog(id);
    }
    
    @ResponseBody
    @RequestMapping(value="getOutputLog/{id}",method=RequestMethod.GET)
    public void getOutputLog(@PathVariable String id) {
        taskService.getOutputLog(id);
    }
}