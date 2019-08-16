package com.yl.distribute.scheduler.resource.service;

import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;
import com.yl.distribute.scheduler.resource.manager.ResourceManager;

public class ResourceServiceImpl implements ResourceService{
    
    @Override
    public String getIdleHost(JobRequest input, String... lastFailedHosts) {
        return ResourceManager.getInstance().getIdleHost(input, lastFailedHosts);
    }
    
    /**
     * 任务执行前增加任务个数
     */
    public void incTask(String serverName,TaskRequest taskRequest) {
        ResourceManager.getInstance().increaseTask(serverName);
    }
    
    /**
     * 任务执行后减少任务个数
     */
    public void decTask(String serverName,TaskRequest taskRequest) {
        ResourceManager.getInstance().decreaseTask(serverName);
    }
    
    public Map<String,HostInfo> getResources() {
        return ResourceManager.getInstance().getResourceMap();
    }
    
    public Map<String,Integer> getTasks() {
        return ResourceManager.getInstance().getTaskMap();
    }

	@Override
	public void updateResource(HostInfo hostInfo) {
		ResourceManager.getInstance().updateResource(hostInfo);
	}
}
