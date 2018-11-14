package com.yl.distribute.scheduler.controller;

import javax.ws.rs.core.*;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.bean.SchedulerResponse;
import com.yl.distribute.scheduler.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/job")
public class JobController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private JobService jobService;
    
    @ResponseBody
    @RequestMapping(value="/addJob",method=RequestMethod.POST)
    public void addJob(@RequestBody JobRequest job) {
        jobService.addJob(job);
    }
    
    @ResponseBody
    @RequestMapping(value="/getJobById/{id}",method=RequestMethod.GET)
    public SchedulerResponse<JobRequest> getJobById(@PathVariable String id) {
       return new SchedulerResponse<JobRequest>(jobService.getJobById(id));
    }
}