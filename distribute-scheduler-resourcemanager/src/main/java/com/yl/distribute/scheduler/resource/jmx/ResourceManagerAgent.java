package com.yl.distribute.scheduler.resource.jmx;
import javax.management.MBeanServer; 
import javax.management.MBeanServerFactory; 
import javax.management.ObjectName; 
//import com.sun.jdmk.comm.HtmlAdaptorServer;

public class ResourceManagerAgent {
    public static void main(String[] args) throws Exception {         
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        ObjectName jobResource = new ObjectName("job:name=ResourceManager");         
        server.registerMBean(new Resource(), jobResource);
//        ObjectName adapterName = new ObjectName("ResourceAgent:name=htmladapter,port=8082");         
//        HtmlAdaptorServer adapter = new HtmlAdaptorServer();         
//        server.registerMBean(adapter, adapterName);
//        adapter.start();         
//        System.out.println("start.....");
    } 
}
