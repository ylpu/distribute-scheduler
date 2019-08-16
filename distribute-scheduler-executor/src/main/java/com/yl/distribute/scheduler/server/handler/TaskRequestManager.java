package com.yl.distribute.scheduler.server.handler;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.server.processor.IServerProcessor;

public class TaskRequestManager {
	
    private static Log LOG = LogFactory.getLog(TaskRequestManager.class);
	
    private static LinkedBlockingQueue<TaskCall> queue = new LinkedBlockingQueue<TaskCall>();
    
    private static final int POOL_SIZE = 2;
	
    private TaskRequestManager() {}
	
    static {
        init();
    }
	
    private static void init() {
        ExecutorService es = null;
        try {
            es = Executors.newFixedThreadPool(POOL_SIZE);
            for(int i = 0; i < POOL_SIZE; i++) {
                es.execute(new TaskExecutor(queue));
            }
        }finally {
            if(es != null) {
               es.shutdown();
            }
        }
    }
	
    public static void addTask(TaskCall call) {
        queue.add(call);
    }
	
    public static LinkedBlockingQueue<TaskCall> getQueue() {
        return queue;
    }
	
    private static final class TaskExecutor implements Runnable{
		
        private LinkedBlockingQueue<TaskCall> queue;
		
        public TaskExecutor(LinkedBlockingQueue<TaskCall> queue) {
            this.queue = queue;
        }
        @Override
        public void run() {
            TaskCall taskCall = null;
            try {
                 while(true) {
                    taskCall = queue.poll();
                    if(taskCall != null) {
                        LOG.info("start to process task " + taskCall.getTaskRequest());
                        process(taskCall);
                    }
                 }
            } catch (Exception e) {
              e.printStackTrace();
            }
        }
		
       private void process(TaskCall taskCall) {
           Class<?> cls = ProcessorManager.getProcessor(taskCall.getTaskRequest().getJob().getJobType());
           IServerProcessor processor = null;
           try {
                processor = (IServerProcessor) cls.getConstructor(taskCall.getTaskRequest().getClass()).newInstance(taskCall.getTaskRequest());
           } catch (Exception e) { 
                LOG.error(e);
                throw new RuntimeException(e);
           }    
           IServerProcessor processorProxy = (IServerProcessor)Proxy.newProxyInstance(processor.getClass().getClassLoader(), processor
               .getClass().getInterfaces(), new ProcessorProxy(processor));  
           processorProxy.execute(taskCall.getCtx());
       }
   } 
}
