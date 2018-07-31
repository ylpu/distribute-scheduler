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
    public Map<String, HostInfo> getResourceMap() {
        return ResourceManager.getInstance().getResourceMap();
    }

    @Override
    public String getRootPool() {
        return ResourceManager.getInstance().getRootPool();
    }
	
    @Override
    public Map<String, Integer> getTaskMap() {
        return ResourceManager.getInstance().getTaskMap();
    }		
}