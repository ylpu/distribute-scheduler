package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

public class ResourceRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }
 
    public void setClassName(String className) {
        this.className = className;
    }
 
    public String getMethodName() {
        return methodName;
    }
 
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
 
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
 
    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
 
    public Object[] getParameters() {
        return parameters;
    }
 
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
