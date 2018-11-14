package com.yl.distribute.scheduler.controller;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.yl.distribute.scheduler.common.bean.SchedulerResponse;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.utils.IOUtils;
import com.yl.distribute.scheduler.service.TaskService;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/task")
public class TaskController {
	
    @Autowired
    private TaskService taskService;

    @Autowired
    private HttpServletResponse response;

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
    @RequestMapping(value="getErrorLog",method=RequestMethod.GET)
    public void getErrorLog(@RequestParam("url") String url) {
    	IOUtils.downloadByUrl(response, url);
    }
    
    @ResponseBody
    @RequestMapping(value="getOutputLog",method=RequestMethod.GET)
    public void getOutputLog(@RequestParam("url") String url) {
    	IOUtils.downloadByUrl(response, url);
    }    
}