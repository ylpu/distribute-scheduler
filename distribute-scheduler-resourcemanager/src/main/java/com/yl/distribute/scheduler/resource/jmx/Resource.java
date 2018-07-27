package com.yl.distribute.scheduler.resource.jmx;

import java.util.List;
import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.resource.manager.ResourceManager;

public class Resource implements ResourceMBean{

    @Override
    public Map<String, List<String>> getPoolServers() {
        return ResourceManager.getInstance().getPoolServers();
    }

    @Override
    public void setPoolServers(Map<String, List<String>> poolServers) {
        ResourceManager.getInstance().setPoolServers(poolServers);
		
    }

    @Override
    public Map<String, HostInfo> getResourceMap() {
        return ResourceManager.getInstance().getResourceMap();
    }

    @Override
    public void setResourceMap(Map<String, HostInfo> resourceMap) {
        ResourceManager.getInstance().setResourceMap(resourceMap);;
		
    }

    @Override
    public String getRootPool() {
        return ResourceManager.getInstance().getRootPool();
    }

    @Override
    public void setRootPool(String rootPool) {
        ResourceManager.getInstance().setRootPool(rootPool);
		
    }
	
    @Override
    public Map<String, Integer> getTaskMap() {
        return ResourceManager.getInstance().getTaskMap();
    }

    @Override
    public void setTaskMap(Map<String, Integer> taskMap) {
        ResourceManager.getInstance().setTaskMap(taskMap);
        
    }		
}