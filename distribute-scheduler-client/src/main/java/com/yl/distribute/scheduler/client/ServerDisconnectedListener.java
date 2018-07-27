package com.yl.distribute.scheduler.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.IZkChildListener;
import com.yl.distribute.scheduler.core.zk.ZKHelper;
import io.netty.channel.pool.SimpleChannelPool;

public class ServerDisconnectedListener {
   public String poolPrefix = "/root"; 
    
    //key is poolname,value is server list
    public Map<String,List<String>> poolServers = new HashMap<String,List<String>>();   
    
    public void init() {
        init(poolPrefix);
    }
    
    public void init(String rootPath) {
        ZkClient zkClient = ZKHelper.getClient();
        List<String> childs = zkClient.getChildren(rootPath);
        if(childs != null && childs.size() > 0) {
            for(String poolName : childs) {
                initPool(zkClient,rootPath + "/" + poolName);
                addNodeChangeListener(zkClient,rootPath + "/" + poolName);  
            }
        }
    }
    
    public void initPool(ZkClient zkClient,String path) {
        List<String> childs = zkClient.getChildren(path);  
        List<String> servers = new ArrayList<String>();
        servers = poolServers.get(path);
        if(servers == null) {
            servers = new ArrayList<String>();
            poolServers.put(path, servers);
        }
        if(childs != null && childs.size() > 0) {
            for(String childPath : childs) { 
                servers.add(childPath);  
            }
        }         
    }
    
    public void addNodeChangeListener(final ZkClient zkClient,final String path) {
        List<String> oldChilds = new ArrayList<String>();
        if(poolServers.get(path) != null) {
            for(String childPath : poolServers.get(path)) {
                oldChilds.add(childPath);
            }
        }
        zkClient.subscribeChildChanges(path, new IZkChildListener() {              
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {  
                System.out.println(String.format("[ZookeeperRegistry] service list change: path=%s, currentChilds=%s",
                        parentPath, currentChilds.toString()));  
                resetOldChilds(oldChilds,currentChilds);
                closeDisconnectedPool(zkClient,parentPath,oldChilds,currentChilds);                 
                System.out.println("Servers: " + poolServers.get(path).toString());  
            }  
        });          
    }  
    
    private void resetOldChilds(List<String> oldChilds,List<String> currentChilds) {
        oldChilds.clear();
        if(currentChilds != null && currentChilds.size() > 0) {
            for(String child : currentChilds) {
                oldChilds.add(child);
            }
        }
    }
    
    /**
     * 关闭已经失联的连接池
     * @param zkClient
     * @param parentPath
     * @param oldChilds
     * @param currentChilds
     */
    public void closeDisconnectedPool(ZkClient zkClient,String parentPath,List<String> oldChilds,final List<String> currentChilds) {
        List<String> servers = poolServers.get(parentPath);
        servers.clear();  
        if(currentChilds != null && currentChilds.size() > 0) {
            for(String child : currentChilds) {  
                servers.add(child);  
            }
        }        
        List<String> disconnectedChilds = getDisconnectedChilds(oldChilds,currentChilds);
        Map<String, SimpleChannelPool> poolMap = SchedulerClientPool.getInstance().getChannelPoolMap();
        if(disconnectedChilds != null && disconnectedChilds.size() > 0) {
            for(String server : disconnectedChilds) {
                SimpleChannelPool pool = poolMap.remove(server);
                if(pool != null) {
                    pool.close();
                }                
            }
        }
    }
    
    private List<String> getDisconnectedChilds(List<String> oldChilds,List<String> currentChilds){
        return oldChilds.stream().filter(t-> !currentChilds.contains(t)).collect(Collectors.toList());
    }   
}
