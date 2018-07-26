package com.yl.distribute.scheduler.client.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.client.callback.ResourceCallback;
import com.yl.distribute.scheduler.common.bean.ResourceRequest;
import com.yl.distribute.scheduler.common.bean.ResourceResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResourceClientHandler extends SimpleChannelInboundHandler<ResourceResponse> {
	
	private static Log LOG = LogFactory.getLog(ResourceClientHandler.class);

    private Channel channel;

    //request Id 与 response的映射
    private Map<Long, ResourceCallback> responseMap = new ConcurrentHashMap<Long, ResourceCallback>();
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ResourceResponse response) throws Exception {
        ResourceCallback callback = responseMap.get(response.getId());
        if (callback != null) {
            responseMap.remove(response.getId());
            callback.setResponse(response);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        channel = ctx.channel();
    }
    
    /**
     * if channel is inactive, then try to reconnect it
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inactive" + ctx.channel());    
    }


    /**
     * if caught exception, then close the channel
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error(cause);
        ctx.close();
    }

    public ResourceResponse invoke(ResourceRequest request) throws Exception {
        ResourceCallback callback = new ResourceCallback();
        responseMap.put(request.getId(), callback);
        channel.writeAndFlush(request);
        return callback.getResponse();
    }
}