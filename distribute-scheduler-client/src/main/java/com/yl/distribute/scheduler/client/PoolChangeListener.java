package com.yl.distribute.scheduler.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.IZkChildListener;
import com.yl.distribute.scheduler.core.zk.ZKHelper;
import io.netty.channel.pool.SimpleChannelPool;

public class PoolChangeListener {
    
   public String poolPrefix = "/root"; 
    
    public void init() {
        init(poolPrefix);
    }
    
    public void init(String rootPath) {
        ZkClient zkClient = ZKHelper.getClient();
        List<String> children = zkClient.getChildren(rootPath);
        if(children != null && children.size() > 0) {
            for(String poolName : children) {
                String poolPath = rootPath + "/" + poolName;
                addNodeChangeListener(zkClient,poolPath);  
            }
        }
    }
    

    
    public void addNodeChangeListener(final ZkClient zkClient,final String poolPath) {
        List<String> oldChildren = new ArrayList<String>();
        List<String> children = zkClient.getChildren(poolPath);        
        if(children != null && children.size() > 0) {
            for(String childPath : children) {
                oldChildren.add(childPath);
            }
        }
        
        zkClient.subscribeChildChanges(poolPath, new IZkChildListener() {              
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {  
                System.out.println(String.format("[ZookeeperRegistry] service list change: path=%s, currentChilds=%s",
                        parentPath, currentChildren.toString()));  
                closeChannelPool(zkClient,parentPath,oldChildren,currentChildren);
                resetOldChildren(oldChildren,currentChildren);                                 
            }  
        });          
    }  
    
    private void resetOldChildren(List<String> oldChildren,List<String> currentChildren) {
        oldChildren.clear();
        if(currentChildren != null && currentChildren.size() > 0) {
            for(String child : currentChildren) {
                oldChildren.add(child);
            }
        }
    }
    
    /**
     * 更新pool机器列表
     * 关闭已经失联的连接池
     * @param zkClient
     * @param parentPath
     * @param oldChilds
     * @param currentChilds
     */
    public void closeChannelPool(ZkClient zkClient,String parentPath,List<String> oldChilds,final List<String> currentChilds) {
       
        List<String> disconnectedChildren = getDisconnectedChildren(oldChilds,currentChilds);
        Map<String, SimpleChannelPool> poolMap = SchedulerClientPool.getInstance().getChannelPoolMap();
        if(disconnectedChildren != null && disconnectedChildren.size() > 0) {
            for(String server : disconnectedChildren) {
                SimpleChannelPool pool = poolMap.remove(server);
                if(pool != null) {
                    pool.close();
                }                
            }
        }
    }
    
    private List<String> getDisconnectedChildren(List<String> oldChildren,List<String> currentChildren){
        return oldChildren.stream().filter(t-> !currentChildren.contains(t)).collect(Collectors.toList());
    }   
}