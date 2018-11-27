package com.yl.distribute.scheduler.server.handler;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.server.processor.IServerProcessor;
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
        TaskTracker.addTask(new TaskCall(ctx,task));
        process(ctx,task);
    } 
    
    /**
     * if caught exception, then close the channel 
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error(cause);
        ctx.close();
    }
    
    private void process(ChannelHandlerContext ctx, TaskRequest task) {
        Class<?> cls = ProcessorManager.getProcessor(task.getJob().getJobType());
        IServerProcessor processor = null;
        try {
            processor = (IServerProcessor) cls.getConstructor(task.getClass()).newInstance(task);
        } catch (Exception e) { 
            LOG.error(e);
            throw new RuntimeException(e);
        }    
        IServerProcessor processorProxy = (IServerProcessor)Proxy.newProxyInstance(processor.getClass().getClassLoader(), processor
                .getClass().getInterfaces(), new ProcessorProxy(processor));  
        processorProxy.execute(ctx);
    }
}