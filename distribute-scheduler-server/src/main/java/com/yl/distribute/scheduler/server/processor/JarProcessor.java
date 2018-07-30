package com.yl.distribute.scheduler.server.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yl.distribute.scheduler.common.bean.TaskRequest;

import io.netty.channel.ChannelHandlerContext;

public class JarProcessor implements IServerProcessor{
    
    private static final Log LOG = LogFactory.getLog(JarProcessor.class);    
 
    private TaskRequest task;
    
    public JarProcessor(TaskRequest task) {
        this.task = task;
    }
    
    @Override
    public void execute(ChannelHandlerContext channel) {
        
    }
}
