package com.yl.distribute.scheduler.server.processor;

//import java.util.Random;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.*;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.utils.IOUtils;
import io.netty.channel.ChannelHandlerContext;

public class CommandProcessor extends CommonServerProcessor implements IServerProcessor{
    
    private static final Log LOG = LogFactory.getLog(CommandProcessor.class);    
 
    private TaskRequest task;
    
    public CommandProcessor(TaskRequest task) {        
        super(task);
        this.task = task;
    }
    
    @Override
    public void execute(ChannelHandlerContext ctx){        
        
        String errorFile = task.getTaskId() + "_error";
        String outPutFile = task.getTaskId() + "_out";    
                      
        try {
//            Thread.sleep(new Random().nextInt(30000));
            if(StringUtils.isNotBlank(task.getJob().getCommand())) {
            	
                Process process = Runtime.getRuntime().exec(task.getJob().getCommand());
                IOUtils.writeOuput(process.getInputStream(),outPutFile);
                IOUtils.writeOuput(process.getErrorStream(),errorFile);  
                //update task to running
                setRunningTask(outPutFile,errorFile);
                Response updateResponse = updateTask(task);
                
                if(updateResponse.getStatus() != 200) {
                    throw new RuntimeException("failed to update task for " + task.getTaskId());
                }
                
                int c = process.waitFor();
                if(c != 0){
                    updateAndWrite(ctx,TaskStatus.FAILED);
                }else {
                    updateAndWrite(ctx,TaskStatus.SUCCESS);
                } 
            }else {
                LOG.warn("command is empty for " + task.getTaskId());
                updateAndWrite(ctx,TaskStatus.SUCCESS);
            }
        }catch (Exception e) {
            LOG.error(e);
            updateAndWrite(ctx,TaskStatus.FAILED);
            System.out.println("after process for " +  task.getTaskId());
        }
    }
 }