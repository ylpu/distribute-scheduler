package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

public class ResourceResponse implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private int errorCode = 200;
    private String errorMsg;
    private Object result;
 
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }    
    
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getResult() {
        return result;
    }
 
    public void setResult(Object result) {
        this.result = result;
    }
}
