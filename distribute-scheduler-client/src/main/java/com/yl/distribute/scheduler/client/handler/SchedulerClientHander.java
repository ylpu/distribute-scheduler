package com.yl.distribute.scheduler.client.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.client.callback.ClientCallback;
import com.yl.distribute.scheduler.common.bean.JobResponse;
import com.yl.distribute.scheduler.common.utils.CallBackUtils;
import com.yl.distribute.scheduler.core.handler.CommonChannelInboundHandler;
import io.netty.channel.ChannelHandlerContext;

public class SchedulerClientHander extends CommonChannelInboundHandler{
	
	private static Log LOG = LogFactory.getLog(SchedulerClientHander.class);
	
    /**
     * read the message from server
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {    
        JobResponse output = (JobResponse)msg;
        ClientCallback callBack = CallBackUtils.getCallback(output.getJobId());        
        callBack.onRead((JobResponse)msg);
    }    
    
    /**
     * if idlestatehandler is set,following method will triggered
     */
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {    
       super.userEventTriggered(ctx,evt);
    }   
 }