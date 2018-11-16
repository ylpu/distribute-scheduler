package com.yl.distribute.scheduler.core.zk;

import java.io.IOException;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;

public class ZKHelper {
    
    private static final String DEFAULT_ZKSERVERS = "127.0.0.1:2181";    
    
    public static ZkClient getClient() {
        return getClient(DEFAULT_ZKSERVERS);
    }
    
    public static ZkClient getClient(String zkServers) {
        return new ZkClient(zkServers, 60000, 5000,new ZkObjectSerializer());
    }
    
    public static void createNode(ZkClient zk,String path,Object data){
        createNode(zk,path,data,CreateMode.PERSISTENT);
    }
    
    public static void createEphemeralNode(ZkClient zk,String path,Object data){
        createNode(zk,path,data,CreateMode.EPHEMERAL);
    }
    
    public static void createNode(ZkClient zk,String path,Object data,CreateMode mode){
        zk.create(path,data,Ids.OPEN_ACL_UNSAFE,mode);
    }
    
    public static void setData(ZkClient zk,String path,Object data){
        zk.writeData(path, data);
    }
    
    public static <T> T getData(ZkClient zk,String path){
        return zk.readData(path, true);
    }
    
    public static void delete(ZkClient zk,String path){
        zk.delete(path, -1);
    }
    
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException{
       ZkClient zkClient = ZKHelper.getClient();
       HostInfo hostInfo = new HostInfo();
       hostInfo.setTotalCores(MetricsUtils.getAvailiableProcessors());
       hostInfo.setTotalMemory(MetricsUtils.getTotalMemInfo());
       hostInfo.setAvailableCores(MetricsUtils.getAvailiableProcessors());
       hostInfo.setAvailableMemory(MetricsUtils.getFreeMemInfo());
       hostInfo.setIp(MetricsUtils.getHostIpAddress() + ":" + 8081);
       hostInfo.setHostName(MetricsUtils.getHostName() + ":" + 8081);
//       zkClient.createEphemeral("/root/pool1/BIH-D-6253:8081", hostInfo);
       hostInfo.setAvailableMemory(MetricsUtils.getFreeMemInfo() - 1000);
       
       try {
    	   ZKHelper.createEphemeralNode(zkClient, "/root", null); 
       }catch(Exception e) {
    	   if(e instanceof ZkNodeExistsException) {
    		   ZkNodeExistsException nee = (ZkNodeExistsException)e;
    		   System.out.println(nee);
    	   }
       }
       
       
//       ZKHelper.setData(zkClient, "/root/pool1/BIH-D-6253:8081", hostInfo);
//       HostInfo obj = ZKHelper.getData(zkClient, "/root/pool1/BIH-D-6253:8081");
//       System.out.println(obj.getAvailableMemory());
       
    }
}
