package com.yl.distribute.scheduler.server.processor;

import java.util.HashMap;
import java.util.Map;
import com.yl.distribute.scheduler.common.bean.TaskRequest;

import io.netty.channel.ChannelHandlerContext;

/**
 * 任务管理
 *
 */
public class TaskManager {    
    
    private static Map<TaskRequest,ChannelHandlerContext> taskMap = new HashMap<TaskRequest,ChannelHandlerContext>();
    

    public static void addTask(TaskCall call) {
        taskMap.put(call.getTaskRequest(), call.getCtx());
    }

    public static void removeTask(TaskRequest task) {
        taskMap.remove(task);
    }
    
    public static Map<TaskRequest, ChannelHandlerContext> getTaskMap() {
        return taskMap;
    }  
}
