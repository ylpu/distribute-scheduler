package com.yl.distribute.scheduler.server;

import java.net.InetSocketAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.server.handler.TaskCall;
import com.yl.distribute.scheduler.server.handler.TaskRequestManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TaskServerHandler extends SimpleChannelInboundHandler<TaskRequest> {    

    private static Log LOG = LogFactory.getLog(TaskServerHandler.class);
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("active channel" + ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        LOG.warn("disconnected with " + clientIP);   
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TaskRequest task) throws Exception {
        TaskRequestManager.addTask(new TaskCall(ctx,task));
    } 
    
    /**
     * if caught exception, then close the channel 
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	    if(ctx != null) {
    	       ctx.close();
    	    }
    }
}