package com.yl.distribute.scheduler.client.handler;

import com.yl.distribute.scheduler.client.callback.ClientCallback;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyClientHander extends ChannelInboundHandlerAdapter{
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
        System.out.println("read with exception " + cause.getMessage());
    }
    
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {    
        
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("Client: "+ctx.channel()+" READER_IDLE 璇昏秴鏃�");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("Client: "+ctx.channel()+" WRITER_IDLE 鍐欒秴鏃�");
            } else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("Client: "+ctx.channel()+" ALL_IDLE 鎬昏秴鏃�");
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }   
 }