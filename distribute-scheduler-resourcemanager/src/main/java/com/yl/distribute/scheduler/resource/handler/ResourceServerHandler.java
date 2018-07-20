package com.yl.distribute.scheduler.resource.handler;

import java.lang.reflect.Method;
import java.util.List;
import com.yl.distribute.scheduler.common.bean.ResourceRequest;
import com.yl.distribute.scheduler.common.bean.ResourceResponse;
import com.yl.distribute.scheduler.core.scan.ClasspathPackageScanner;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResourceServerHandler extends SimpleChannelInboundHandler<ResourceRequest> {

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, ResourceRequest request) throws Exception {
        ResourceResponse response = new ResourceResponse();
        response.setId(request.getId());
        try {
            Object result = invoke(request);
            response.setResult(result);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        ctx.writeAndFlush(response);
    }

    public Object invoke(ResourceRequest request) throws Exception {
        String classname = request.getClassName();
        String methodname = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Object o = getServiceImplementation(classname);
        Class<?> clazz = o.getClass();
        Method method = clazz.getMethod(methodname, parameterTypes);
        Object result = method.invoke(o, parameters);
        return result;
    }
    
    private Object getServiceImplementation(String classname) {
        try {
            Class<?> cls = Class.forName(classname);
            List<String> classenames = ClasspathPackageScanner.getClassNames();
            if(classenames != null && classenames.size() > 0) {
                for(String fullName : classenames) {
                    Class<?> cls1 = Class.forName(fullName);
                    if(cls.isAssignableFrom(cls1)) {
                        return Class.forName(cls1.getName()).newInstance();
                    }
                } 
                throw new RuntimeException("can not find service implementation for " + classname);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("can not find service implementation for " + classname);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
