package com.yl.distribute.scheduler.server.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import io.netty.channel.ChannelHandlerContext;

public class ScriptProcessor implements IServerProcessor{
    
    private static final Log LOG = LogFactory.getLog(ScriptProcessor.class);    
    
    private TaskRequest task;
    
    public ScriptProcessor(TaskRequest task) {
        this.task = task;
    }
    @Override
    public void execute(ChannelHandlerContext channel) {
        
    }
}
