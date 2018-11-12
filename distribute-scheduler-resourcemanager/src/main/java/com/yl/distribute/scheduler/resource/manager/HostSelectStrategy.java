package com.yl.distribute.scheduler.resource.manager;

import com.yl.distribute.scheduler.common.bean.JobRequest;

public interface HostSelectStrategy {
    
    public String getIdleHost(ResourceManager rm,JobRequest request,String... lastFailedHosts);

}
