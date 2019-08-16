package com.yl.distribute.scheduler.resource.handler;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.ResourceRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResourceServerHandler extends SimpleChannelInboundHandler<ResourceRequest> {
	
	private static Log LOG = LogFactory.getLog(ResourceServerHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ResourceRequest request) throws Exception {        
        new ResourceServerProcessor(ctx,request).process();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(ctx != null) {
        	   ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        LOG.warn("disconnected with " + clientIP);
    }
}
