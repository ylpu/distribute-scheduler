package com.yl.distribute.scheduler.resource.manager;

import com.yl.distribute.scheduler.common.bean.JobConf;

public interface HostSelectStrategy {
    
    public String getIdleHost(ResourceManager rm,JobConf request,String... lastFailedHosts);

}
