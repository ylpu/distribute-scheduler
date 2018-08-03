package com.yl.distribute.scheduler.client.callback;

import java.util.concurrent.LinkedBlockingQueue;
import com.yl.distribute.scheduler.common.bean.TaskResponse;

public class ResponseQueue {
    
    private static LinkedBlockingQueue<TaskResponse> taskQueue = new LinkedBlockingQueue<TaskResponse>();
    
    public static void add(TaskResponse response) {
        taskQueue.add(response);
    }

    public static LinkedBlockingQueue<TaskResponse> getTaskQueue() {
        return taskQueue;
    } 
}
