package com.yl.distribute.scheduler.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import com.yl.distribute.scheduler.client.ResourceClient;
import com.yl.distribute.scheduler.common.bean.ResourceRequest;
import com.yl.distribute.scheduler.common.bean.ResourceResponse;
import com.yl.distribute.scheduler.core.config.Configuration;

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

        if (client == null) {
            Properties prop = Configuration.getConfig("config.properties");        
            String resourceServer = Configuration.getString(prop, "resource.manager.server");
            int resourcePort = Configuration.getInt(prop, "resource.manager.port");
            client = ResourceClient.getConnect(resourceServer, resourcePort);
        }
        ResourceResponse r = client.invoke(request);
        return r.getResult();
    }

    public void close() {
        this.client.closeConnect();
    }
}