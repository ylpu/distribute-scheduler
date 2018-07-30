package com.yl.distribute.scheduler.server.processor;

import java.util.Date;
import java.util.Properties;
import javax.ws.rs.core.Response;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import io.netty.channel.ChannelHandlerContext;

public abstract class CommonServerProcessor {
    
    private TaskRequest task;
    
    public CommonServerProcessor(TaskRequest task) {
        this.task = task;
    }
    
    public void setRunningTask(String stdoutFile,String stderrorFile) {    
        Properties prop = Configuration.getConfig("config.properties");        
        int port = Configuration.getInt(prop, "jetty.server.port");
        //客户端可以根据url读取jetty服务器上的errorFile
        task.setErrorOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/server/" + stderrorFile);
        //客户端可以根据url读取jetty服务器上的outPutFile
        task.setStdOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/server/" + stdoutFile);
        task.setTaskStatus(TaskStatus.RUNNING);
    }
    
    public void updateAndWrite(ChannelHandlerContext ctx,TaskStatus taskStatus) {
        
        task.setTaskStatus(taskStatus);
        task.setEndTime(new Date());
        updateTask(task);
        
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTaskId(task.getTaskId());   
        response.setTaskStatus(taskStatus);                  
        ctx.writeAndFlush(response);
    }
    
    public Response updateTask(TaskRequest task) {
        Properties prop = Configuration.getConfig("config.properties");        
        String taskApi = Configuration.getString(prop, "task.web.api");
        return JerseyClient.update(taskApi + "/" + "updateTask", task);
    }
}
