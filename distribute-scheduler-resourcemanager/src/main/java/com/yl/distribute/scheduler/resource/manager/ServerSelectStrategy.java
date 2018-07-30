package com.yl.distribute.scheduler.resource.manager;

import com.yl.distribute.scheduler.common.bean.JobConf;

public interface ServerSelectStrategy {
    
    public String getIdleServer(JobConf request,ResourceManager rm,String... lastFailedServers);

}
