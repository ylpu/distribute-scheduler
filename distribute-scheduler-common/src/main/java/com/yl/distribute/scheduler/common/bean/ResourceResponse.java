package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

public class ResourceResponse implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String error;
    private Object result;
 
    public boolean isError() {
        return error != null;
    }
 
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }
 
    public void setError(String error) {
        this.error = error;
    }
 
    public Object getResult() {
        return result;
    }
 
    public void setResult(Object result) {
        this.result = result;
    }
}
