package com.yl.distribute.scheduler.server.handler;

import com.yl.distribute.scheduler.common.bean.TaskRequest;

import io.netty.channel.ChannelHandlerContext;

public class TaskCall {
    
    private TaskRequest taskRequest;
    private ChannelHandlerContext ctx;
    
    public TaskCall(ChannelHandlerContext ctx,TaskRequest taskRequest){
        this.taskRequest = taskRequest;
        this.ctx = ctx;        
    }

    public TaskRequest getTaskRequest() {
        return taskRequest;
    }

    public void setTaskRequest(TaskRequest taskRequest) {
        this.taskRequest = taskRequest;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
