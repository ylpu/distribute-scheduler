package com.yl.distribute.scheduler.server.processor;

import org.apache.commons.lang3.StringUtils;

import com.yl.distribute.scheduler.common.bean.TaskRequest;
import io.netty.channel.ChannelHandlerContext;

public class JavaProcessor extends CommonTaskProcessor implements IServerProcessor{    
 
    private TaskRequest task;
    
    public JavaProcessor(TaskRequest task) {        
        super(task);
        this.task = task;
    }
    
    @Override
    public void execute(ChannelHandlerContext ctx){     
        executeTask(ctx,buildCommand());
       
    }

    @Override
    public String buildCommand() {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(task.getJob().getCommand());
        commandBuilder.append(" ");
        if(StringUtils.isNotBlank(task.getJob().getClasspath())) {
            commandBuilder.append("cp ").append(task.getJob().getClasspath());
            commandBuilder.append(" ");
        }        
        commandBuilder.append(task.getJob().getExecuteParameters());
        commandBuilder.append(" ");
        commandBuilder.append(task.getJob().getCommandParameters());
        return commandBuilder.toString();
    }
}
