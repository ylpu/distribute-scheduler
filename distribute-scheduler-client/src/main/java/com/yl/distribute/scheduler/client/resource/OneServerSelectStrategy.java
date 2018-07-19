package com.yl.distribute.scheduler.client.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobRequest;

/**
 * 从pool中选择资源最多的一台机器提交任务
 *
 */
public class OneServerSelectStrategy implements ServerSelectStrategy{

    @Override
    public String getIdleServer(JobRequest input,Map<String,List<String>> poolServers,Map<String,HostInfo> resourceMap,String lastFailedServer) {
        List<String> servers = poolServers.get(ResourceManager.getInstance().getRootPool() + "/" + input.getPoolName());
        List<HostInfo> sortedServers = new ArrayList<HostInfo>();
        if(servers != null && servers.size() > 0){
            for(String server : servers) {
                if(resourceMap.get(server) != null) {
                    sortedServers.add(resourceMap.get(server));
                }
            }
            Collections.sort(sortedServers);
            if(sortedServers != null && sortedServers.size() > 0) {
                if(StringUtils.isEmpty(lastFailedServer)) {
                    return sortedServers.get(0).getHostName();
                }else {
                    //任务重试会选择没有失败并且资源最多的server,如果没有可用server就抛出异常
                    List<HostInfo> excludeServers = sortedServers.stream().filter(
                            hostInfo -> !hostInfo.getHostName().equalsIgnoreCase(lastFailedServer))
                            .collect(Collectors.toList());
                    if(excludeServers != null && excludeServers.size() > 0) {
                        return excludeServers.get(0).getHostName();
                    }else {
                        throw new RuntimeException("找不到可用的服务器 "+ input.getRequestId());
                    }
                }
                
            }
        }
        throw new RuntimeException("can not find availiable server");
    }
}
