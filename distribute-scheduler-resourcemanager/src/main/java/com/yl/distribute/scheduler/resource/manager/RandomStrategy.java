package com.yl.distribute.scheduler.resource.manager;

import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import com.yl.distribute.scheduler.common.bean.JobConf;

/**
 * 从pool中随机选一台机器去提交任务
 *
 */
public class RandomStrategy implements HostSelectStrategy{
    @Override
    public String getIdleHost(ResourceManager rm,JobConf request,String... lastFailedHosts) {
        List<String> servers = rm.getPoolServers().get(request.getPoolPath());
        String idleServer = "";
        if(servers != null && servers.size() > 0){
            String[] keys = rm.getResourceMap().keySet().toArray(new String[0]);
            Random random = new Random();
            String randomServer = keys[random.nextInt(keys.length)];
            idleServer = rm.getResourceMap().get(randomServer).getHostName();
        }
        if(!StringUtils.isEmpty(idleServer)) {
           return idleServer;
        }
        throw new RuntimeException("can not find availiable server");
    }
}