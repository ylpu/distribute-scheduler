package com.yl.distribute.scheduler.resource.manager;

import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;

/**
 * 从pool中随机选一台机器去提交任务
 *
 */
public class RandomServerSelectStrategy implements ServerSelectStrategy{

    @Override
    public String getIdleServer(JobRequest input,Map<String,List<String>> poolServers,Map<String,HostInfo> resourceMap,String lastFailedServer) {
        List<String> servers = poolServers.get(ResourceManager.getInstance().getRootPool() + "/" + input.getPoolName());
        String idleServer = "";
        if(servers != null && servers.size() > 0){
            String[] keys = resourceMap.keySet().toArray(new String[0]);
            Random random = new Random();
            String randomServer = keys[random.nextInt(keys.length)];
            idleServer = resourceMap.get(randomServer).getHostName();
        }
        if(!StringUtils.isEmpty(idleServer)) {
           return idleServer;
        }
        throw new RuntimeException("can not find availiable server");
    }
}