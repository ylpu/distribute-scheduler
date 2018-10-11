package com.yl.distribute.scheduler.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class CommonChannelInboundHandler extends ChannelInboundHandlerAdapter{
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {    
        
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println(ctx.channel()+" READER_IDLE 超时");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println(ctx.channel()+" WRITER_IDLE 超时");
            } else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println(ctx.channel()+" ALL_IDLE 超时");
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    } 
}
