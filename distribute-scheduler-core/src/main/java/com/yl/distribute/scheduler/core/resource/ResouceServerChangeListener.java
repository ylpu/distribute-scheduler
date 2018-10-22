package com.yl.distribute.scheduler.core.resource;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.IZkChildListener;
import com.esotericsoftware.minlog.Log;
import com.yl.distribute.scheduler.core.zk.ZKHelper;

public class ResouceServerChangeListener {  
    
    private static final String DEFAULT_ZKSERVERS = "127.0.0.1:2181";
    
    public String rmPath = "/rm"; 
   
    private String rmServer = "";
    
    private static ResouceServerChangeListener resouceServerChangeListener = null;   
   
    private ResouceServerChangeListener(String zookeeperServers){
        init(zookeeperServers);
    }
    
    private ResouceServerChangeListener(){
        init(DEFAULT_ZKSERVERS);
    }
    
    public static synchronized ResouceServerChangeListener getInstance() {
        if(resouceServerChangeListener == null){
            resouceServerChangeListener = new ResouceServerChangeListener();
        }
        return resouceServerChangeListener;
    }
    
    public static synchronized ResouceServerChangeListener getInstance(String zookeeperServers) {
        if(resouceServerChangeListener == null){
            resouceServerChangeListener = new ResouceServerChangeListener(zookeeperServers);
        }
        return resouceServerChangeListener;
    }
    
    public void init(String zookeeperServers) {
        ZkClient zkClient = ZKHelper.getClient(zookeeperServers);
        List<String> children = zkClient.getChildren(rmPath);
        if(children == null || children.size() == 0){
            throw new RuntimeException("can not get active resource manager server");
        }
        rmServer = children.get(0);
        addNodeChangeListener(zkClient,rmPath);        
    }
    
    public void addNodeChangeListener(final ZkClient zkClient,final String rmPath) {        
        zkClient.subscribeChildChanges(rmPath, new IZkChildListener() {              
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {  
                System.out.println(String.format("[ZookeeperRegistry] service list change: path=%s, currentChilds=%s",
                        parentPath, currentChildren.toString()));
                if(currentChildren != null && currentChildren.size() >0){
                    rmServer = currentChildren.get(0);
                } else{
                    Log.warn("current child is empty, can not get active resource manager");
                }                     
            }  
        });          
    }

    public String getRmServer() {
        return rmServer;
    }
    
    public static void main(String[] args){
        System.out.println(ResouceServerChangeListener.getInstance().getRmServer());
    }
}