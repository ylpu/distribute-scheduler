package com.yl.distribute.scheduler.server.processor;

import com.yl.distribute.scheduler.common.bean.TaskRequest;
import io.netty.channel.ChannelHandlerContext;

public class TaskCall {
    
    private ChannelHandlerContext ctx;
    private TaskRequest task;
    
    public TaskCall(ChannelHandlerContext ctx,TaskRequest task) {
        this.ctx = ctx;
        this.task = task;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public TaskRequest getTask() {
        return task;
    }

    public void setTask(TaskRequest task) {
        this.task = task;
    }    
}
