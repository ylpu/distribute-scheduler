package com.yl.distribute.scheduler.resource.jmx;

import java.util.List;
import java.util.Map;
import com.yl.distribute.scheduler.common.bean.HostInfo;

public interface ResourceMBean {
	
	public Map<String, List<String>> getPoolServers();
	
	public void setPoolServers(Map<String, List<String>> poolServers);

	public Map<String, HostInfo> getResourceMap();

	public void setResourceMap(Map<String, HostInfo> resourceMap);
	
    public String getRootPool();

    public void setRootPool(String rootPool); 

}
