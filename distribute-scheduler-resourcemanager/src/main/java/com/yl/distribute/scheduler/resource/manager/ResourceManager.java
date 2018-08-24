package com.yl.distribute.scheduler.resource.manager;

import java.util.ArrayList;
import java.util.Arrays;
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
    
    //key is servername,value is host info
    public Map<String,HostInfo> resourceMap = new HashMap<String,HostInfo>();   
    
    //key is servername,value is tasknumbers
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
                initPool(zkClient,rootPool + "/" + poolName);
                addNodeChangeListener(zkClient,rootPool + "/" + poolName);  
            }
        }
    }
    
    private void initPool(ZkClient zkClient,String path) {
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
                addServers(zkClient,path,Arrays.asList(childPath));
            }
        }         
    }
    /**
     * zk中节点有变动更新pool中机器
     * @param zkClient
     * @param path
     */
    public void addNodeChangeListener(final ZkClient zkClient,final String path) {
        List<String> oldChilds = new ArrayList<String>();
        if(poolServers.get(path) != null) {
            for(String childPath : poolServers.get(path)) {
                oldChilds.add(childPath);
            }
        }
        zkClient.subscribeChildChanges(path, new IZkChildListener() {              
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception { 
                LOG.warn(String.format("[ZookeeperRegistry] service list change: path=%s, currentChilds=%s",
                        parentPath, currentChilds.toString())); 
                refreshPool(zkClient,parentPath,oldChilds,currentChilds); 
                resetOldChild(oldChilds,currentChilds);
                System.out.println("Servers: " + poolServers.get(path).toString());  
            }  
        });          
    }  
    /**
     * 把新的节点付给老的节点
     * @param oldChilds
     * @param currentChilds
     */
    private void resetOldChild(List<String> oldChilds,List<String> currentChilds) {
        oldChilds.clear();
        if(currentChilds != null && currentChilds.size() > 0) {
            for(String child : currentChilds) {
                oldChilds.add(child);
            }
        }
    }
    /**
     * 刷新pool
     * 给新节点加上channel池
     * 关闭断掉的节点连接池
     * @param zkClient
     * @param parentPath
     * @param oldChilds
     * @param currentChilds
     */
    public synchronized void refreshPool(ZkClient zkClient,String parentPath,List<String> oldChilds,final List<String> currentChilds) {
        List<String> servers = poolServers.get(parentPath);
        servers.clear();  
        if(currentChilds != null && currentChilds.size() > 0) {
            for(String child : currentChilds) {  
                servers.add(child);  
            }
        }        
        List<String> newChilds = getNewChilds(oldChilds,currentChilds);
        addServers(zkClient,parentPath,newChilds);
        List<String> disconnectedChilds = getDisconnectedChilds(oldChilds,currentChilds);
        removeServers(disconnectedChilds);
    }
    
    private List<String> getDisconnectedChilds(List<String> oldChilds,List<String> currentChilds){
        return oldChilds.stream().filter(t-> !currentChilds.contains(t)).collect(Collectors.toList());
    }
    
    private List<String> getNewChilds(List<String> oldChilds,List<String> currentChilds){
        return currentChilds.stream().filter(t-> !oldChilds.contains(t)).collect(Collectors.toList());
    }
    
    private synchronized void removeServers(List<String> disconnectedChilds) {
        if(disconnectedChilds != null && disconnectedChilds.size() > 0) {
            for(String child : disconnectedChilds) {
                resourceMap.remove(child);
            }
        }
    }
    /**
     * 为每台机器添加固定个channel池
     * @param zkClient
     * @param path
     * @param childs
     */
    private synchronized void addServers(ZkClient zkClient,String path,List<String> childs) {
        if(childs != null && childs.size() > 0) {
            for(String child : childs) {                
                HostInfo serverData = zkClient.readData(path + "/" + child);             
                resourceMap.put(child, serverData);     
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
     * @param lastFailedServer
     * @return
     */
    public synchronized String getIdleServer(JobConf input,String... lastFailedServers) {
        ServerSelectStrategy serverSelectStrategy = ResourceStrategy.getStrategy(input.getStrategy());
        if(serverSelectStrategy == null){
            throw new RuntimeException("can not find strategy class for " + input.getStrategy());
        }
        return new ResourceStrategyContext(serverSelectStrategy).select(this,input,lastFailedServers);

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