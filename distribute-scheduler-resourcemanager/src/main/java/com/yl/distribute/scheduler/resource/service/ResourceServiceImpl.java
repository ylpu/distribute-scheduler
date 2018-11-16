package com.yl.distribute.scheduler.resource.service;

import java.util.Map;
import java.util.Properties;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.redis.RedisClient;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;
import com.yl.distribute.scheduler.resource.manager.ResourceManager;

public class ResourceServiceImpl implements ResourceService{
    
    private static final String REDIS_CONFIG = "redis.properties";

    @Override
    public String getIdleHost(JobRequest input, String... lastFailedHosts) {
        return ResourceManager.getInstance().getIdleHost(input, lastFailedHosts);
    }
    
    /**
     * 任务执行完恢复资源
     * 首先恢复redis资源
     * 其次恢复内存资源
     */
    public void addResource(String serverName,TaskRequest taskRequest) {
        
        ResourceManager.getInstance().addResource(serverName, taskRequest.getJob());
        ResourceManager.getInstance().decreaseTask(serverName);
    }
    
    /**
     * 任务执行前申请资源
     * 首先减掉redis资源
     * 其次减掉内存资源
     */
    public void subResource(String serverName,TaskRequest taskRequest) {
        
        Properties prop = Configuration.getConfig(REDIS_CONFIG);
        RedisClient.getInstance(prop).getAndSub(taskRequest);
        
        ResourceManager.getInstance().subResource(serverName, taskRequest.getJob());
        ResourceManager.getInstance().increaseTask(serverName);
    }
    
    public Map<String,HostInfo> getResources() {
        return ResourceManager.getInstance().getResourceMap();
    }
    
    public Map<String,Integer> getTasks() {
        return ResourceManager.getInstance().getTaskMap();
    }
}
