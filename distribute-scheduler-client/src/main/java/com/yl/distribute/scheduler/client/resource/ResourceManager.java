package com.yl.distribute.scheduler.client.resource;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.IZkChildListener;
import com.yl.distribute.scheduler.client.NettyPoolClient;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.constants.GlobalConstants;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.zk.*;

import io.netty.channel.pool.SimpleChannelPool;

public class ResourceManager {
    
    public String rootPool = "/root";
    
    //key is machine pool name,value is server list
    public Map<String,List<String>> poolServers = new ConcurrentHashMap<String,List<String>>();    
   
    //key is servername,value is channel pool
    public Map<String,SimpleChannelPool> channelPoolMap = new ConcurrentHashMap<String,SimpleChannelPool>();
    
    //key is servername,value is host info
    public Map<String,HostInfo> resourceMap = new HashMap<String,HostInfo>();
    
    private static ResourceManager resourceManager = new ResourceManager(); 
    
    private NettyPoolClient client = NettyPoolClient.getInstance();
    
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
                addServerAndChannels(zkClient,path,Arrays.asList(childPath));
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
                System.out.println(String.format("[ZookeeperRegistry] service list change: path=%s, currentChilds=%s",
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
        addServerAndChannels(zkClient,parentPath,newChilds);
        List<String> disconnectedChilds = getDisconnectedChilds(oldChilds,currentChilds);
        removeServerAndChannels(disconnectedChilds);
    }
    
    private List<String> getDisconnectedChilds(List<String> oldChilds,List<String> currentChilds){
        return oldChilds.stream().filter(t-> !currentChilds.contains(t)).collect(Collectors.toList());
    }
    
    private List<String> getNewChilds(List<String> oldChilds,List<String> currentChilds){
        return currentChilds.stream().filter(t-> !oldChilds.contains(t)).collect(Collectors.toList());
    }
    
    private void removeServerAndChannels(List<String> disconnectedChilds) {
        if(disconnectedChilds != null && disconnectedChilds.size() > 0) {
            for(String child : disconnectedChilds) {
                channelPoolMap.get(child).close();
                channelPoolMap.remove(child);
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
    private synchronized void addServerAndChannels(ZkClient zkClient,String path,List<String> childs) {
        if(childs != null && childs.size() > 0) {
            Properties prop = Configuration.getConfig("config.properties");        
            int poolNumber = Configuration.getInt(prop, "channel.pool.numbers");
            for(String child : childs) {                
                HostInfo serverData = zkClient.readData(path + "/" + child);
                client.build(poolNumber);
                SimpleChannelPool pool = client.poolMap.get(new InetSocketAddress(serverData.getIpAddress().split(":")[0],
                        Integer.parseInt(serverData.getIpAddress().split(":")[1])));                
                resourceMap.put(child, serverData);
                channelPoolMap.put(child, pool);
            }
        }
    }
    /**
     * 处理任务时减少资源
     * @param serverName
     * @param resourceParams
     */
    public synchronized void subResource(String serverName,Map<String,Object> resourceParams) {
        int usedCores = 0;
        long usedMemory = 0;
        synchronized(resourceMap){
            HostInfo hostInfo = resourceMap.get(serverName);
            usedCores = (resourceParams == null || resourceParams.get("cores") == null) ? 
                    GlobalConstants.DEFAULT_CORE_SIZE : Integer.parseInt(String.valueOf(resourceParams.get("cores")));
            usedMemory = (resourceParams == null || resourceParams.get("memory") == null) ? 
                    GlobalConstants.DEFAUTL_MEMEORY : Long.parseLong(String.valueOf(resourceParams.get("memory")));
            hostInfo.setCores(hostInfo.getCores() - usedCores);
            hostInfo.setMemory(hostInfo.getMemory() - usedMemory);  
        }
    }
    
    /**
     * 任务处理完毕恢复资源
     * @param serverName
     * @param resourceParams
     */
    public void addResource(String serverName,Map<String,Object> resourceParams) {
        int usedCores = 0;
        long usedMemory = 0;
        synchronized(resourceMap){
            HostInfo hostInfo = resourceMap.get(serverName);
            usedCores = (resourceParams == null || resourceParams.get("cores") == null) ? 
                    GlobalConstants.DEFAULT_CORE_SIZE : Integer.parseInt(String.valueOf(resourceParams.get("cores")));
            usedMemory = (resourceParams == null || resourceParams.get("memory") == null) ? 
                    GlobalConstants.DEFAUTL_MEMEORY : Long.parseLong(String.valueOf(resourceParams.get("memory")));
            hostInfo.setCores(hostInfo.getCores() + usedCores);
            hostInfo.setMemory(hostInfo.getMemory() + usedMemory); 
        }
    }
    /**
     * 根据策略获取pool中的空闲机器 
     * @param input
     * @param lastFailedServer
     * @return
     */
    public synchronized String getIdleServer(JobRequest input,String lastFailedServer) { 
        Properties prop = Configuration.getConfig("config.properties"); 
        String serverStrategy = Configuration.getString(prop, "idle.server.select.strategy");
        ServerSelectStrategy serverSelectStrategy = null;
        try {
            serverSelectStrategy = (ServerSelectStrategy) Class.forName(serverStrategy).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverSelectStrategy.getIdleServer(input, poolServers, resourceMap, lastFailedServer);

    }
    
    public SimpleChannelPool getIdleServerChannel(String serverName) { 
       return channelPoolMap.get(serverName);
    }

    public String getRootPool() {
        return rootPool;
    }

    public void setRootPool(String rootPool) {
        this.rootPool = rootPool;
    }   
}