package com.yl.distribute.scheduler.core.zk;

import org.I0Itec.zkclient.ZkClient;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;

public class ZKResourceManager {
    
    public static synchronized void subZkResource(TaskRequest task){
        String path = task.getJob().getPoolPath() + "/" + task.getRunningHost();
        ZkClient zkClient = ZKHelper.getClient();        
        HostInfo hostInfo = (HostInfo) ZKHelper.getData(zkClient, path);
        long usedMemory = MetricsUtils.getTaskMemory(task.getJob().getExecuteParameters());
        System.out.println("host memory sub " + (hostInfo.getAvailableMemory() - usedMemory) + " for task " + task.getTaskId());
        hostInfo.setAvailableMemory(hostInfo.getAvailableMemory() - usedMemory);
        ZKHelper.setData(zkClient, path, hostInfo);
        zkClient.close();
    }
    
    public static synchronized void restoreZkResource(TaskRequest task){
        String path = task.getJob().getPoolPath() + "/" + task.getRunningHost();
        ZkClient zkClient = ZKHelper.getClient();        
        HostInfo hostInfo = (HostInfo) ZKHelper.getData(zkClient, path);
        long usedMemory = MetricsUtils.getTaskMemory(task.getJob().getExecuteParameters());
        System.out.println("host memory add " + (hostInfo.getAvailableMemory() + usedMemory) + " for task " + task.getTaskId());
        hostInfo.setAvailableMemory(hostInfo.getAvailableMemory() + usedMemory);        
        ZKHelper.setData(zkClient, path, hostInfo);
        zkClient.close();
    }
}
