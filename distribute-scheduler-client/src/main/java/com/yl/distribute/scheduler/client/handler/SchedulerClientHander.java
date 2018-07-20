package com.yl.distribute.scheduler.client.handler;

import com.yl.distribute.scheduler.client.callback.ClientCallback;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import com.yl.distribute.scheduler.core.handler.CommonChannelInboundHandler;
import io.netty.channel.ChannelHandlerContext;


public class SchedulerClientHander extends CommonChannelInboundHandler{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {    
        System.out.println("read message is " + msg);
        JobResponse output = (JobResponse)msg;
        ClientCallback callBack = CallBackUtils.getCallback(output.getResponseId());        
        callBack.onRead((JobResponse)msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inactive" + ctx.channel());       
       
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("read channel" + ctx + "with exception " + cause.getMessage());
    }
    
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {    
       super.userEventTriggered(ctx,evt);
    }   
 }