package com.yl.distribute.scheduler.server.processor;

import com.yl.distribute.scheduler.common.bean.TaskRequest;
import io.netty.channel.ChannelHandlerContext;

public class JarProcessor extends CommonServerProcessor implements IServerProcessor{    
 
    private TaskRequest task;
    
    public JarProcessor(TaskRequest task) {        
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
        commandBuilder.append(task.getJob().getCommandParameters());
        commandBuilder.append(" ");
        commandBuilder.append(task.getJob().getExecuteParameters());
        return commandBuilder.toString();
    }
}
