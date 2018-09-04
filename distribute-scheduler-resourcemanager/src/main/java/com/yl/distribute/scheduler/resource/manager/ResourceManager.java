package com.yl.distribute.scheduler.resource.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.I0Itec.zkclient.IZkChildListener;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import com.yl.distribute.scheduler.core.zk.*;

public class ResourceManager{
    
    private static Log LOG = LogFactory.getLog(ResourceManager.class);
    
    private static ResourceManager resourceManager = new ResourceManager(); 
    
    public String rootPool = "/root";
    
    //key is machine pool name,value is server list
    public Map<String,List<String>> poolServers = new ConcurrentHashMap<String,List<String>>(); 
    
    //key is hostname,value is host info
    public Map<String,HostInfo> resourceMap = new HashMap<String,HostInfo>();   
    
    //key is hostname,value is tasknumbers
    public Map<String,Integer> taskMap = new HashMap<String,Integer>();
        
    
    private ResourceManager() {        
    }
    
    public static ResourceManager getInstance() {
        return resourceManager;
    }
    
    public void init() {
        init(rootPool);
    }
    
    public void init(String rootPool) {
        this.rootPool = rootPool;
        ZkClient zkClient = ZKHelper.getClient();
        List<String> pools = zkClient.getChildren(rootPool);
        if(pools != null && pools.size() > 0) {
            for(String poolName : pools) {
                String poolPath = rootPool + "/" + poolName;
                initPool(zkClient,poolPath);
                addNodeChangeListener(zkClient,poolPath);  
            }
        }
    }
    
    private void initPool(ZkClient zkClient,String poolPath) {          
        List<String> servers = poolServers.get(poolPath);
        if(servers == null) {
            servers = new ArrayList<String>();
            poolServers.put(poolPath, servers);
        }
        List<String> children = zkClient.getChildren(poolPath);
        if(children != null && children.size() > 0) {
            for(String nodePath : children) { 
                servers.add(nodePath);  
                setResource(zkClient,poolPath,nodePath);
            }
        }         
    }
    /**
     * zk中节点有变动更新pool中机器
     * @param zkClient
     * @param poolPath
     */
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
                LOG.warn(String.format("[ZookeeperRegistry] service list change: path=%s, currentChildren=%s",
                        parentPath, currentChildren.toString())); 
                refreshPool(zkClient,parentPath,oldChildren,currentChildren); 
                resetOldChild(oldChildren,currentChildren);
            }  
        });          
    }  
    /**
     * 把新的节点付给老的节点
     * @param oldChildren
     * @param currentChildren
     */
    private void resetOldChild(List<String> oldChildren,List<String> currentChildren) {
        oldChildren.clear();
        if(currentChildren != null && currentChildren.size() > 0) {
            for(String child : currentChildren) {
                oldChildren.add(child);
            }
        }
    }
    /**
     * 刷新pool
     * @param zkClient
     * @param poolPath
     * @param oldChildren
     * @param currentChildren
     */
    public synchronized void refreshPool(ZkClient zkClient,String poolPath,List<String> oldChildren,List<String> currentChildren) { 
        poolServers.put(poolPath, currentChildren);
        List<String> newChildren = getNewChildren(oldChildren,currentChildren);
        setResource(zkClient,poolPath,newChildren.toArray(new String[newChildren.size()]));
        List<String> disconnectedChildren = getRemovedChildren(oldChildren,currentChildren);
        removeResource(disconnectedChildren);
    }
    
    private List<String> getRemovedChildren(List<String> oldChildren,List<String> currentChildren){
        return oldChildren.stream().filter(t-> !currentChildren.contains(t)).collect(Collectors.toList());
    }
    
    private List<String> getNewChildren(List<String> oldChildren,List<String> currentChildren){
        return currentChildren.stream().filter(t-> !oldChildren.contains(t)).collect(Collectors.toList());
    }
    
    private synchronized void removeResource(List<String> disconnectedChildren) {
        if(disconnectedChildren != null && disconnectedChildren.size() > 0) {
            for(String child : disconnectedChildren) {
                resourceMap.remove(child);
            }
        }
    }
    /**
     * 为每台机器添加资源信息
     * @param zkClient
     * @param poolPath
     * @param children
     */
    private synchronized void setResource(ZkClient zkClient,String poolPath,String... children) {
        if(children != null && children.length > 0) {
            for(String child : children) {                
                HostInfo hostInfo = zkClient.readData(poolPath + "/" + child);             
                resourceMap.put(child, hostInfo);     
            }
        }
    }
    /**
     * 处理任务时减少资源
     * @param serverName
     * @param resourceParams
     */
    public void subResource(String serverName,JobConf jobConf) {
        long usedMemory = 0;
        synchronized(resourceMap){
            HostInfo hostInfo = resourceMap.get(serverName);
            usedMemory = MetricsUtils.getTaskMemory(jobConf.getExecuteParameters());
            hostInfo.setAvailableCores(hostInfo.getAvailableCores() - 1);
            hostInfo.setAvailableMemory(hostInfo.getAvailableMemory() - usedMemory);  
        }
    }
    
    /**
     * 任务处理完毕恢复资源
     * @param serverName
     * @param resourceParams
     */
    public void addResource(String serverName,JobConf jobConf) {
        long usedMemory = 0;
        synchronized(resourceMap){
            HostInfo hostInfo = resourceMap.get(serverName);
            usedMemory = MetricsUtils.getTaskMemory(jobConf.getExecuteParameters());
            hostInfo.setAvailableCores(hostInfo.getAvailableCores() + 1);
            hostInfo.setAvailableMemory(hostInfo.getAvailableMemory() + usedMemory); 
        }
    }
    
    /**
     * 开始处理任务
     * @param serverName
     * @param taskMap
     */
    public void increaseTask(String serverName) {
        synchronized(taskMap){
            if(taskMap.get(serverName) != null) {
                taskMap.put(serverName, taskMap.get(serverName) + 1);
            }else {
                taskMap.put(serverName, 1);
            }
        }
    }
    
    /**
     * 处理完任务
     * @param serverName
     * @param taskMap
     */
    public void decreaseTask(String serverName) {
        synchronized(taskMap){
            if(taskMap.get(serverName) != null) {
                taskMap.put(serverName, taskMap.get(serverName) - 1);
            }
        }
    }
    /**
     * 根据策略获取pool中的空闲机器 
     * @param input
     * @param lastFailedHosts
     * @return
     */
    public synchronized String getIdleHost(JobConf input,String... lastFailedHosts) {
        HostSelectStrategy hostSelectStrategy = ResourceStrategy.getStrategy(input.getStrategy());
        if(hostSelectStrategy == null){
            throw new RuntimeException("can not find strategy class for " + input.getStrategy());
        }
        return new ResourceStrategyContext(hostSelectStrategy).select(this,input,lastFailedHosts);

    }

    public String getRootPool() {
        return rootPool;
    }

    public void setRootPool(String rootPool) {
        this.rootPool = rootPool;
    }

	public Map<String, List<String>> getPoolServers() {
		return poolServers;
	}

	public void setPoolServers(Map<String, List<String>> poolServers) {
		this.poolServers = poolServers;
	}

	public Map<String, HostInfo> getResourceMap() {
		return resourceMap;
	}

	public void setResourceMap(Map<String, HostInfo> resourceMap) {
		this.resourceMap = resourceMap;
	}

    public Map<String, Integer> getTaskMap() {
        return taskMap;
    }

    public void setTaskMap(Map<String, Integer> taskMap) {
        this.taskMap = taskMap;
    }    	
}