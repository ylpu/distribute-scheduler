package com.yl.distribute.scheduler.resource.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobConf;

/**
 * 从pool中选择任务最少的一台机器提交任务
 *
 */
public class TaskIdleStrategy implements ServerSelectStrategy{

    @Override
    public String getIdleServer(JobConf request,ResourceManager rm,String... lastFailedServers) {
        
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
            
            List<Entry<String, Integer>> runningServers = taskSortlist.stream().filter(
                    entry -> !Arrays.asList(lastFailedServers).contains(entry.getKey()))
                    .collect(Collectors.toList());
            if(runningServers != null && runningServers.size() > 0) {
                return resourceMap.get(runningServers.get(runningServers.size()-1).getKey()).getHostName();
            }else {
                throw new RuntimeException("找不到可用的服务器 "+ request.getJobId());
            }
        }
        throw new RuntimeException("can not find availiable server");
    }
}
