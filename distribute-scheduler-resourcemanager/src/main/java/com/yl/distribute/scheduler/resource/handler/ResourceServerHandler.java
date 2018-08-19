package com.yl.distribute.scheduler.resource.handler;

import java.net.InetSocketAddress;

import com.yl.distribute.scheduler.common.bean.ResourceRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResourceServerHandler extends SimpleChannelInboundHandler<ResourceRequest> {

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, ResourceRequest request) throws Exception {        
        new ResourceServerProcessor(ctx,request).process();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        System.out.println("disconnected with " + clientIP);    
    }
}
