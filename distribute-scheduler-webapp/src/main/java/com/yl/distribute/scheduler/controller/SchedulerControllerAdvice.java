package com.yl.distribute.scheduler.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.yl.distribute.scheduler.common.bean.SchedulerResponse;

@ControllerAdvice
public class SchedulerControllerAdvice {
	
	private static final Log log = LogFactory.getLog(SchedulerControllerAdvice.class);
	
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public SchedulerResponse<?> errorHandler(Exception e) {        
        log.error(e);
        return new SchedulerResponse<>(500,"系统内部错误");
    }
 }
