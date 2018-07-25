package com.yl.distribute.scheduler.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;
import com.yl.distribute.scheduler.client.ResourceClient;
import com.yl.distribute.scheduler.common.bean.ResourceRequest;
import com.yl.distribute.scheduler.common.bean.ResourceResponse;


public class ResourceProxy implements InvocationHandler {

    private static AtomicLong id = new AtomicLong(0);

    private ResourceClient client = null;

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ResourceProxy()
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ResourceRequest request = new ResourceRequest();
        request.setId(id.incrementAndGet());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        client = new ResourceClient().connect();        
        ResourceResponse r = client.invoke(request);
        return r.getResult();
    }

    public void close() {
        this.client.closeConnect();
    }
}