package com.yl.distribute.scheduler.server.processor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.yl.distribute.scheduler.core.resource.rpc.ResourceProxy;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.utils.ReflectUtils;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;

public class ProcessorProxy implements InvocationHandler{
    
    private static final Log LOG = LogFactory.getLog(ProcessorProxy.class);
    
    private Object obj;
    
    public ProcessorProxy(Object obj)
    {
        this.obj = obj;
    }
    
    @Override
    public Object invoke(Object object, Method method, Object[] args){  
        TaskRequest task = null;
        try {
            task = ReflectUtils.getFieldValue(obj, "task");
            method.invoke(obj, args);
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }finally {
            try {
                LOG.info("start to release resource for " + task.getRunningServer());
                System.out.println("start to release resource for " + task.getRunningServer());
                ResourceService service = ResourceProxy.get(ResourceService.class);
                service.addResource(task.getRunningServer(), task.getJob());
                service.decreaseTask(task.getRunningServer());
            } catch (Exception e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }finally {
                long endTime = System.currentTimeMillis();
                LOG.info("cost " + (endTime - task.getStartTime().getTime())/1000 
                        + " seconds to execute task " + task.getTaskId() + "-" + task.getId());
                System.out.println("cost " + (endTime - task.getStartTime().getTime())/1000 
                        + " seconds to execute task " + task.getTaskId() + "-" + task.getId());
            }
        }        
        return null;
    }
}