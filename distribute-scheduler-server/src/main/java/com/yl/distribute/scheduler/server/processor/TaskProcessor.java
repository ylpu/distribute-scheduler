package com.yl.distribute.scheduler.server.processor;

import java.lang.reflect.Proxy;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TaskProcessor {
    
    private static Log LOG = LogFactory.getLog(TaskProcessor.class);
    
    private static LinkedBlockingQueue<TaskCall> queue = new LinkedBlockingQueue<TaskCall>();
    
    static {
        new TaskHandler(queue).start();
    }
    
    public static void addTask(TaskCall call) {
        queue.offer(call);
    }   
    
    public static LinkedBlockingQueue<TaskCall> getQueue() {
        return queue;
    }

    public static class TaskHandler extends Thread{
        
        private LinkedBlockingQueue<TaskCall> queue;
        
        public TaskHandler(LinkedBlockingQueue<TaskCall> queue) {
            this.queue = queue;
        }
        
        public void run() {
            TaskCall call = null;
            while (true) {
                if((call = queue.poll()) != null) {
                    Class<?> cls = ProcessorManager.getProcessor(call.getTask().getJob().getJobType());
                    IServerProcessor processor = null;
                    try {
                        processor = (IServerProcessor) cls.getConstructor(call.getTask().getClass()).newInstance(call.getTask());
                    } catch (Exception e) { 
                        LOG.error(e);
                    }    
                    IServerProcessor processorProxy = (IServerProcessor)Proxy.newProxyInstance(processor.getClass().getClassLoader(), processor
                            .getClass().getInterfaces(), new ProcessorProxy(processor));  
                    processorProxy.execute(call.getCtx());
                }
            }
        }
    }
}
