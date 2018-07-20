package com.yl.distribute.scheduler.client.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yl.distribute.scheduler.client.callback.ResourceCallback;
import com.yl.distribute.scheduler.common.bean.ResourceRequest;
import com.yl.distribute.scheduler.common.bean.ResourceResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResourceHandler extends SimpleChannelInboundHandler<ResourceResponse> {

    private Channel channel;

    //request Id 与 response的映射
    private Map<Long, ResourceCallback> responseMap = new ConcurrentHashMap<Long, ResourceCallback>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ResourceResponse response) throws Exception {
        ResourceCallback holder = responseMap.get(response.getId());
        if (holder != null) {
            responseMap.remove(response.getId());
            holder.setResponse(response);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        channel = ctx.channel();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }

    public ResourceResponse invoke(ResourceRequest request) throws Exception {
        ResourceCallback holder = new ResourceCallback();
        responseMap.put(request.getId(), holder);
        channel.writeAndFlush(request);
        return holder.getResponse();
    }

}
