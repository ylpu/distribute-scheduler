package com.yl.distribute.scheduler.resource.jmx;

import java.util.List;
import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;

public interface ResourceMBean {
	
    public Map<String, List<String>> getPoolServers();	

    public Map<String, HostInfo> getResourceMap();
	
    public String getRootPool();
    
    public Map<String, Integer> getTaskMap();    

}