package com.yl.distribute.scheduler.resource.service;

import java.util.Map;

import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.core.redis.RedisUtil;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;
import com.yl.distribute.scheduler.resource.manager.ResourceManager;

public class ResourceServiceImpl implements ResourceService{

    @Override
    public String getIdleHost(JobConf input, String... lastFailedHosts) {
        return ResourceManager.getInstance().getIdleHost(input, lastFailedHosts);
    }
    
    public void addResource(String serverName,JobConf jobConf) {
    	RedisUtil.restoreResource(jobConf);
        ResourceManager.getInstance().addResource(serverName, jobConf);
        ResourceManager.getInstance().decreaseTask(serverName);
    }
    
    public void subResource(String serverName,JobConf jobConf) {
    	RedisUtil.subResource(jobConf);
        ResourceManager.getInstance().subResource(serverName, jobConf);
        ResourceManager.getInstance().increaseTask(serverName);
    }
    
    public Map<String,HostInfo> getResources() {
        return ResourceManager.getInstance().getResourceMap();
    }
    
    public Map<String,Integer> getTasks() {
        return ResourceManager.getInstance().getTaskMap();
    }
}
