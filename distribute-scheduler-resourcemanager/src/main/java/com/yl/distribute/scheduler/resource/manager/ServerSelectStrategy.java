package com.yl.distribute.scheduler.resource.manager;

import com.yl.distribute.scheduler.common.bean.JobRequest;

public interface ServerSelectStrategy {
    
    public String getIdleServer(JobRequest request,String lastFailedServer,ResourceManager rm);

}
