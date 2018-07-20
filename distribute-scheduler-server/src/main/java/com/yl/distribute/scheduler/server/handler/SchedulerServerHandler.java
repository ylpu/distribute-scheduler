package com.yl.distribute.scheduler.server.handler;

import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.server.processor.CommandProcessor;
import com.yl.distribute.scheduler.server.processor.IServerProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class SchedulerServerHandler extends SimpleChannelInboundHandler<JobRequest> {    

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("disconnected from remote address " + ctx.channel().remoteAddress());        
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, JobRequest request) throws Exception {
        System.out.println("execute command " + request.getCommand() + " for requestId " + request.getRequestId());
        IServerProcessor processor = new CommandProcessor(request);
        processor.execute(ctx);
    } 
}
