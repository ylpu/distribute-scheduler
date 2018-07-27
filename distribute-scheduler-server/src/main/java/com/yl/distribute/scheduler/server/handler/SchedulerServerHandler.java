package com.yl.distribute.scheduler.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.server.processor.CommandProcessor;
import com.yl.distribute.scheduler.server.processor.IServerProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class SchedulerServerHandler extends SimpleChannelInboundHandler<JobRequest> {    

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
    public void channelRead0(ChannelHandlerContext ctx, JobRequest request) throws Exception {
    	LOG.info("execute command " + request.getCommand() + " for requestId " + request.getJobId());
        IServerProcessor processor = new CommandProcessor(request);
        processor.execute(ctx);
    } 
}
