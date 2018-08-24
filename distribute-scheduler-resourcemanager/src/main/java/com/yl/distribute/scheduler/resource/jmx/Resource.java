package com.yl.distribute.scheduler.resource.jmx;

import com.yl.distribute.scheduler.common.utils.StringUtils;
import com.yl.distribute.scheduler.resource.manager.ResourceManager;

public class Resource implements ResourceMBean{

    @Override
    public String getPoolServers() {
        return StringUtils.getMapAsString(ResourceManager.getInstance().getPoolServers());
    }

    @Override
    public String getResourceMap() {
        return StringUtils.getResourceMapAsString(ResourceManager.getInstance().getResourceMap());
    }

    @Override
    public String getRootPool() {
        return ResourceManager.getInstance().getRootPool();
    }
	
    @Override
    public String getTaskMap() {
        return StringUtils.getMapAsString(ResourceManager.getInstance().getTaskMap());
    }		
}