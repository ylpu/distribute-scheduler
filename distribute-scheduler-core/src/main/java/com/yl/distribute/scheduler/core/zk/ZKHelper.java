package com.yl.distribute.scheduler.core.zk;

import java.io.IOException;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

public class ZKHelper {
    
    private static final String DEFAULT_ZKSERVERS = "127.0.0.1:2181";    
    
    public static ZkClient getClient() {
        return getClient(DEFAULT_ZKSERVERS);
    }
    
    public static ZkClient getClient(String zkServers) {
        return new ZkClient(zkServers, 60000, 1000,new ZkObjectSerializer());
    }
    
    public static void createNode(ZkClient zk,String node,byte[] data){
        createNode(zk,node,data,CreateMode.PERSISTENT);
    }
    
    public static void createEphemeralNode(ZkClient zk,String node,byte[] data){
        createNode(zk,node,data,CreateMode.EPHEMERAL);
    }
    
    public static void createNode(ZkClient zk,String node,byte[] data,CreateMode mode){
        zk.create(node,data,Ids.OPEN_ACL_UNSAFE,mode);
    }
    
    public static void delete(ZkClient zk,String path){
        zk.delete(path, -1);
    }
    
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException{
       ZkClient zkClient = ZKHelper.getClient();
       ZKHelper.delete(zkClient,"/root/defaultpool/BIH-D-6253-8081");
    }
}
