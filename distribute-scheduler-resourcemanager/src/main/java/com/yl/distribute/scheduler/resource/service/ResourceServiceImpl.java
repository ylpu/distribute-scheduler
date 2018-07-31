package com.yl.distribute.scheduler.resource.service;

import java.util.Map;

import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;
import com.yl.distribute.scheduler.resource.manager.ResourceManager;

public class ResourceServiceImpl implements ResourceService{

    @Override
    public String getIdleServer(JobConf input, String... lastFailedServer) {
        return ResourceManager.getInstance().getIdleServer(input, lastFailedServer);
    }
    
    public void addResource(String serverName,JobConf jobConf) {
        ResourceManager.getInstance().addResource(serverName, jobConf);
    }
    
    public void subResource(String serverName,JobConf jobConf) {
        ResourceManager.getInstance().subResource(serverName, jobConf);
    }
    
    public void increaseTask(String serverName) {
        ResourceManager.getInstance().increaseTask(serverName);
    }
    
    public void decreaseTask(String serverName) {
        ResourceManager.getInstance().decreaseTask(serverName);
    }
    
    public Map<String,HostInfo> getResources() {
        return ResourceManager.getInstance().getResourceMap();
    }
    
    public Map<String,Integer> getTasks() {
        return ResourceManager.getInstance().getTaskMap();
    }
}
