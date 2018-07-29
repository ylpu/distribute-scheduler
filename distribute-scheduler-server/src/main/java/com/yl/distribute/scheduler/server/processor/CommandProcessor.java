package com.yl.distribute.scheduler.server.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
//import java.util.Random;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.*;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import io.netty.channel.ChannelHandlerContext;

public class CommandProcessor implements IServerProcessor{
    
    private static final Log LOG = LogFactory.getLog(CommandProcessor.class);    
 
    private Task task;
    
    public CommandProcessor(Task task) {
        this.task = task;
    }


    @Override
    public void execute(ChannelHandlerContext ctx){        
        
        String errorFile = task.getTaskId() + "_error";
        String outPutFile = task.getTaskId() + "_out"; 
        
        TaskResponse response = new TaskResponse();
        response.setTaskId(task.getTaskId());       
                      
        try {
//            Thread.sleep(new Random().nextInt(30000));
            if(StringUtils.isNotBlank(task.getJob().getCommand())) {
            	
                Process process = Runtime.getRuntime().exec(task.getJob().getCommand());
                generateStreamOutPut(process.getInputStream(),outPutFile);
                generateStreamOutPut(process.getErrorStream(),errorFile);
                
                setTask(outPutFile,errorFile);
                Response updateResponse = updateTask(task);
                if(updateResponse.getStatus() != 200) {
                    throw new RuntimeException("failed to update task for " + task.getTaskId());
                }
                
                int c = process.waitFor();
                if(c != 0){
                	response.setTaskStatus(TaskStatus.FAILED.getStatus());                    
                    ctx.writeAndFlush(response);
                }else {
                	response.setTaskStatus(TaskStatus.SUCCESS.getStatus());                    
                    ctx.writeAndFlush(response);                
                } 
            }else {
                LOG.warn("command is empty for " + task.getTaskId());
                response.setTaskStatus(TaskStatus.SUCCESS.getStatus());            
                ctx.writeAndFlush(response);
            }
        }catch (Exception e) {
            LOG.error(e);
            response.setTaskStatus(TaskStatus.FAILED.getStatus());            
            ctx.writeAndFlush(response);
            System.out.println("after process " +  response.getTaskId());
        }
    }
    
    private void setTask(String stdoutFile,String stderrorFile) {    
        Properties prop = Configuration.getConfig("config.properties");        
        int port = Configuration.getInt(prop, "jetty.server.port");
        //客户端可以根据url读取jetty服务器上的errorFile
        task.setErrorOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/server/" + stderrorFile);
        //客户端可以根据url读取jetty服务器上的outPutFile
        task.setStdOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/server/" + stdoutFile);
        task.setTaskStatus(TaskStatus.RUNNING.getStatus());
    }
    
    private void generateStreamOutPut(InputStream is,String fileName){        
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName));
            BufferedReader brError = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String errline = null;
            while ((errline = brError.readLine()) != null) {
                bw.write(errline);
                bw.flush();
            }
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        } finally {            
            try {
                bw.close();
            } catch (IOException e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }
        }
    }
    
    private Response updateTask(Task task) {
        Properties prop = Configuration.getConfig("config.properties");        
        String taskApi = Configuration.getString(prop, "task.web.api");
        return JerseyClient.update(taskApi + "/" + "updateTask", task);
    }
}