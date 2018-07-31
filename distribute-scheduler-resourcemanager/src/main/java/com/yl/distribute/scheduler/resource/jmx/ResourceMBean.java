package com.yl.distribute.scheduler.resource.jmx;

public interface ResourceMBean {
	
    public String getPoolServers();	

    public String getResourceMap();
	
    public String getRootPool();
    
    public String getTaskMap();    

}