package com.yl.distribute.scheduler.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.Task;
import com.yl.distribute.scheduler.server.processor.CommandProcessor;
import com.yl.distribute.scheduler.server.processor.IServerProcessor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class SchedulerServerHandler extends SimpleChannelInboundHandler<Task> {    

    private static Log LOG = LogFactory.getLog(SchedulerServerHandler.class);
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	LOG.info("active channel" + ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	LOG.warn("disconnected from remote address " + ctx.channel().remoteAddress());        
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Task task) throws Exception {
    	LOG.info("execute command " + task.getJob().getCommand() + " for requestId " + task.getTaskId());
        IServerProcessor processor = new CommandProcessor(task);
        processor.execute(ctx);
    } 
}
