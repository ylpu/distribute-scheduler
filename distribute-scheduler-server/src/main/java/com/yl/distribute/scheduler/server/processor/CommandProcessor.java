package com.yl.distribute.scheduler.server.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.*;
import com.yl.distribute.scheduler.common.config.Configuration;
import com.yl.distribute.scheduler.common.enums.JobStatus;
import com.yl.distribute.scheduler.common.jersey.JerseyClient;
import com.yl.distribute.scheduler.common.utils.JobUtils;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import io.netty.channel.ChannelHandlerContext;

public class CommandProcessor implements IServerProcessor{
    
    private static final Log LOG = LogFactory.getLog(CommandProcessor.class);    
 
    private JobRequest input;
    
    public CommandProcessor(JobRequest input) {
        this.input = input;
    }


    @Override
    public void execute(ChannelHandlerContext ctx){          
        
        String jobId = JobUtils.getJobId(input.getRequestId());
        String errorFile = jobId + "_error";
        String outPutFile = jobId + "_out"; 
        
        JobResponse output = setOutput(outPutFile,errorFile);              
        try {
          //更新任务
            Response response = updateJob(output);
            System.out.println(response);
            if(response.getStatus() != 200) {
                throw new RuntimeException("failed to update job for " + input.getRequestId());
            }
            if(StringUtils.isNotBlank(input.getCommand())) {
                Process process = Runtime.getRuntime().exec(input.getCommand());
                generateStreamOutPut(process.getInputStream(),outPutFile);
                generateStreamOutPut(process.getErrorStream(),errorFile);
                //waitFor()判断Process进程是否终止，通过返回值判断是否正常终止。0代表正常终止
                int c = process.waitFor();
                if(c != 0){
                    output.setJobStatus(JobStatus.FAILED.getStatus());                    
                    ctx.writeAndFlush(output);
                }else {
                    output.setJobStatus(JobStatus.SUCCESS.getStatus());                    
                    ctx.writeAndFlush(output);                
                } 
            }  
        }catch (Exception e) {
            LOG.error(e);
            output.setJobStatus(JobStatus.FAILED.getStatus());            
            ctx.writeAndFlush(output);
            System.out.println("after process " +  output.getResponseId());
        }
    }
    
    private JobResponse setOutput(String outPutFile,String errorFile) {    
        Properties prop = Configuration.getConfig("config.properties");        
        int port = Configuration.getInt(prop, "jetty.server.port");
        int zkPort = Configuration.getInt(prop, "zk.regist.default.port");
        JobResponse response = new JobResponse();
        response.setRunningServer(MetricsUtils.getHostName() + "-" + zkPort);
        response.setResponseId(input.getRequestId());
        response.setErrorOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/" + errorFile);
        response.setStdOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/" + outPutFile);        
        response.setJobStatus(JobStatus.RUNNING.getStatus());
        return response;
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
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {            
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    
    private Response updateJob(JobResponse response) {
        Properties prop = Configuration.getConfig("config.properties");        
        String jobApi = Configuration.getString(prop, "job.web.api");
        return JerseyClient.update(jobApi + "/" + "updateJob", response);
    }
}