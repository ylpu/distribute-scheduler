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
import com.yl.distribute.scheduler.common.enums.JobStatus;
import com.yl.distribute.scheduler.common.utils.JobUtils;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import io.netty.channel.ChannelHandlerContext;

public class CommandProcessor implements IServerProcessor{
    
    private static final Log LOG = LogFactory.getLog(CommandProcessor.class);    
 
    private JobRequest input;
    
    public CommandProcessor(JobRequest input) {
        this.input = input;
    }


    @Override
    public void execute(ChannelHandlerContext ctx){          
        
        String jobId = JobUtils.getJobId(input.getJobId());
        String errorFile = jobId + "_error";
        String outPutFile = jobId + "_out"; 
        
        JobResponse output = setOutput(outPutFile,errorFile);              
        try {
            Response response = updateJob(output);
            if(response.getStatus() != 200) {
                throw new RuntimeException("failed to update job for " + input.getJobId());
            }
//            Thread.sleep(new Random().nextInt(30000));
            if(StringUtils.isNotBlank(input.getCommand())) {
                Process process = Runtime.getRuntime().exec(input.getCommand());
                generateStreamOutPut(process.getInputStream(),outPutFile);
                generateStreamOutPut(process.getErrorStream(),errorFile);
                int c = process.waitFor();
                if(c != 0){
                    output.setJobStatus(JobStatus.FAILED.getStatus());                    
                    ctx.writeAndFlush(output);
                }else {
                    output.setJobStatus(JobStatus.SUCCESS.getStatus());                    
                    ctx.writeAndFlush(output);                
                } 
            }else {
                LOG.warn("command is empty for " + input.getJobId());
                output.setJobStatus(JobStatus.SUCCESS.getStatus());            
                ctx.writeAndFlush(output);
            }
        }catch (Exception e) {
            LOG.error(e);
            output.setJobStatus(JobStatus.FAILED.getStatus());            
            ctx.writeAndFlush(output);
            System.out.println("after process " +  output.getJobId());
        }finally {
            
        }
    }
    
    private JobResponse setOutput(String stdoutFile,String stderrorFile) {    
        Properties prop = Configuration.getConfig("config.properties");        
        int port = Configuration.getInt(prop, "jetty.server.port");
        int zkPort = Configuration.getInt(prop, "zk.regist.default.port");
        JobResponse response = new JobResponse();
        response.setRunningServer(MetricsUtils.getHostName() + "-" + zkPort);
        response.setJobId(input.getJobId());
        //客户端可以根据url读取jetty服务器上的errorFile
        response.setErrorOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/server/" + stderrorFile);
        //客户端可以根据url读取jetty服务器上的outPutFile
        response.setStdOutputUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + port + "/server/" + stdoutFile);        
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
    
    private Response updateJob(JobResponse response) {
        Properties prop = Configuration.getConfig("config.properties");        
        String jobApi = Configuration.getString(prop, "job.web.api");
        return JerseyClient.update(jobApi + "/" + "updateJob", response);
    }
}