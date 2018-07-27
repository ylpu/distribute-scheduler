package com.yl.distribute.scheduler.resource.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;

/**
 * 从pool中选择任务最少的一台机器提交任务
 *
 */
public class TaskIdleStrategy implements ServerSelectStrategy{

    @Override
    public String getIdleServer(JobRequest request,String lastFailedServer,ResourceManager rm) {
        
        List<String> poolServers = rm.getPoolServers().get(request.getPoolPath());
        Map<String, Integer>  poolServerTasks = new HashMap<String, Integer>();
        
        Map<String, HostInfo>  resourceMap =  rm.getResourceMap();
        Map<String, Integer>  taskMap = rm.getTaskMap();        
        
        if(poolServers != null && poolServers.size() > 0) {
            for(String server : poolServers) {
                poolServerTasks.put(server,taskMap.get(server));
            }
        }
        
        List<Entry<String, Integer>> taskSortlist = new ArrayList<Entry<String, Integer>>(poolServerTasks.entrySet()); 
        
        Collections.sort(taskSortlist,new Comparator<Map.Entry<String,Integer>>() {  
            //升序排序  
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
                return o1.getValue().compareTo(o2.getValue());  
            }  
        }); 
        if(taskSortlist != null && taskSortlist.size() > 0) {
            String hostName = resourceMap.get(taskSortlist.get(taskSortlist.size()-1).getKey()).getHostName();
            if(!hostName.equalsIgnoreCase(lastFailedServer)) {
                return hostName;
            }else {
                throw new RuntimeException("can not find availiable server");
            }
        }
        throw new RuntimeException("can not find availiable server");
    }
}
