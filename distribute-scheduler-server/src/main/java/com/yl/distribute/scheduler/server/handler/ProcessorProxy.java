package com.yl.distribute.scheduler.server.handler;

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
            LOG.info("start to execute task " + task.getId());
            method.invoke(obj, args);
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }finally {
            try {
                TaskTracker.removeTask(task);
                releaseResource(task);
            } catch (Exception e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }
        }        
        return null;
    }    

    private void releaseResource(TaskRequest task) {
        LOG.info("start to release resource for " + task.getRunningHost());
        ResourceService service = ResourceProxy.get(ResourceService.class);
        service.addResource(task.getRunningHost(), task);
    }
}