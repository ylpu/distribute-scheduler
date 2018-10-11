package com.yl.distribute.scheduler.server.processor;

import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;

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
        if(task.getJob().getCommand().indexOf("-Xmx") <= 0) {
        	long executeExemory = MetricsUtils.getTaskMemory(task.getJob());
        	commandBuilder.append(" ");
        	commandBuilder.append("-Xms" + executeExemory + "mb");
        	commandBuilder.append(" ");
        	commandBuilder.append("-Xmx" + executeExemory + "mb");
        } 
        return commandBuilder.toString();
    }
}
