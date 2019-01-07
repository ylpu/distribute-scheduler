package com.yl.distribute.scheduler.server.processor;

import io.netty.channel.ChannelHandlerContext;

public interface IServerProcessor{    
    public void execute(ChannelHandlerContext channel);
}
