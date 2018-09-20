package com.yl.distribute.scheduler.server.processor;

import java.util.Properties;
import java.util.Random;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.utils.IOUtils;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.task.TaskManager;
import io.netty.channel.ChannelHandlerContext;

public abstract class CommonTaskProcessor {
    
    private static final Log LOG = LogFactory.getLog(CommonTaskProcessor.class);
    
    private TaskRequest task;
    
    public CommonTaskProcessor(TaskRequest task) {
        this.task = task;
    }
    
    public void executeTask(ChannelHandlerContext ctx,String command){        
        
        String errorFile = "./WebContent/jobfiles/" + task.getTaskId() + "_error";
        String outPutFile = "./WebContent/jobfiles/" + task.getTaskId() + "_out";    
                      
        try {
//        	Thread.sleep(new Random().nextInt(20000));
            if(StringUtils.isNotBlank(command)) {
                
                Process process = Runtime.getRuntime().exec(command);
                IOUtils.writeOuput(process.getInputStream(),outPutFile);
                IOUtils.writeOuput(process.getErrorStream(),errorFile);  
                //update task to running
                setRunningTask(outPutFile,errorFile);
                Response updateResponse = TaskManager.getInstance().updateTask(task);
                
                if(updateResponse.getStatus() != Response.Status.OK.getStatusCode()) {
                    throw new RuntimeException("failed to update task for " + task.getTaskId());
                }
                
                int c = process.waitFor();
                if(c != 0){
                    updateAndResponse(ctx,TaskStatus.FAILED);
                }else {
                    updateAndResponse(ctx,TaskStatus.SUCCESS);
                } 
            }else {
                LOG.warn("command is empty for " + task.getTaskId());
                updateAndResponse(ctx,TaskStatus.SUCCESS);
            }
        }catch (Exception e) {
            LOG.error(e);
            updateAndResponse(ctx,TaskStatus.FAILED);
            System.out.println("after process for " +  task.getTaskId());
        }
    }
    
    public abstract String buildCommand();
    
    public void setRunningTask(String stdoutFile,String stderrorFile) {    
        Properties prop = Configuration.getConfig("config.properties");        
        int port = Configuration.getInt(prop, "jetty.server.port");
        //客户端可以根据url读取jetty服务器上的errorFile
        task.setErrorOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/server/" + stderrorFile);
        //客户端可以根据url读取jetty服务器上的outPutFile
        task.setStdOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/server/" + stdoutFile);
        task.setTaskStatus(TaskStatus.RUNNING);
    }
    
    public void updateAndResponse(ChannelHandlerContext ctx,TaskStatus taskStatus) {
        TaskManager.getInstance().updateTask(task, taskStatus);        
        TaskResponse response = new TaskResponse();
        response.setTaskId(task.getTaskId());   
        response.setFailedTimes(task.getFailedTimes());
        response.setJobId(task.getJob().getJobId());
        response.setTaskStatus(taskStatus);                  
        ctx.writeAndFlush(response);
    }
}
