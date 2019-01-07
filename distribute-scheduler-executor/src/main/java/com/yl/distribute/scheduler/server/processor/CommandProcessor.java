package com.yl.distribute.scheduler.server.processor;

import com.yl.distribute.scheduler.common.bean.*;
import io.netty.channel.ChannelHandlerContext;

public class CommandProcessor extends CommonTaskProcessor implements IServerProcessor{
    
    private TaskRequest task;
    
    public CommandProcessor(TaskRequest task) {        
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
        return commandBuilder.toString();
    }
 }