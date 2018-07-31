package com.yl.distribute.scheduler.resource.jmx;
import javax.management.MBeanServer; 
import javax.management.MBeanServerFactory;
import javax.management.ObjectName; 
import com.sun.jdmk.comm.HtmlAdaptorServer;

public class ResourceManagerAgent {
    
    public void start() throws Exception {
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        ObjectName jobResource = new ObjectName("com.yl.distribute.job:name=ResourceManager");         
        server.registerMBean(new Resource(), jobResource);
        ObjectName adapterName = new ObjectName("ResourceAgent:name=htmladapter,port=9090");         
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();         
        server.registerMBean(adapter, adapterName);
        adapter.setPort(9090); 
        adapter.start();         
    }
}
