package com.yl.distribute.scheduler.client.handler;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yl.distribute.scheduler.client.callback.TaskCallback;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import com.yl.distribute.scheduler.core.handler.CommonChannelInboundHandler;

import io.netty.channel.ChannelHandlerContext;

public class SchedulerClientHander extends CommonChannelInboundHandler{
	
    private static Log LOG = LogFactory.getLog(SchedulerClientHander.class);
	
    /**
     * read the message from server
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {    
        TaskResponse output = (TaskResponse)msg;
        TaskCallback callBack = CallBackUtils.getCallback(output.getTaskId());        
        callBack.onRead((TaskResponse)msg);
    } 
    

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        LOG.error("disconnected with " + clientIP);
        System.out.println("disconnected with " + clientIP);    
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error(cause);
    }
    
    /**
     * if idlestatehandler is set,following method will be triggered
     */
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {    
       super.userEventTriggered(ctx,evt);
    }   
 }