package com.yl.distribute.scheduler.client.callback;

import com.yl.distribute.scheduler.common.bean.ResourceResponse;

public class ResourceCallback {
    
    private ResourceResponse response;

    public void setResponse(ResourceResponse response) {
        this.response = response;
        synchronized (this) {
            notify();
        }
    }

    public ResourceResponse getResponse() throws Exception {
        synchronized (this) {
            wait();
        }
        return response;
    }

    public ResourceResponse getResponse(long timeout) throws Exception {
        synchronized (this) {
            wait();
        }
        return response;
    }
}
