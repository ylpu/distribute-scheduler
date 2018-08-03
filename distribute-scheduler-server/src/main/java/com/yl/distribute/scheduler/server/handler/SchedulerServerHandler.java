package com.yl.distribute.scheduler.server.handler;

import java.lang.reflect.Proxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.server.processor.IServerProcessor;
import com.yl.distribute.scheduler.server.processor.ProcessorManager;
import com.yl.distribute.scheduler.server.processor.ProcessorProxy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class SchedulerServerHandler extends SimpleChannelInboundHandler<TaskRequest> {    

    private static Log LOG = LogFactory.getLog(SchedulerServerHandler.class);
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	LOG.info("active channel" + ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	LOG.warn("disconnected from remote address " + ctx.channel().remoteAddress());        
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TaskRequest task) throws Exception {  
        Class<?> cls = ProcessorManager.getProcessor(task.getJob().getJobType());
        IServerProcessor processor = (IServerProcessor) cls.getConstructor(task.getClass()).newInstance(task);
    	IServerProcessor processorProxy = (IServerProcessor)Proxy.newProxyInstance(processor.getClass().getClassLoader(), processor
                .getClass().getInterfaces(), new ProcessorProxy(processor));    	
    	processorProxy.execute(ctx);
    } 
}
