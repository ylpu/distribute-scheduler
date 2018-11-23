package com.yl.distribute.scheduler.core.resource;

import java.util.Date;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.I0Itec.zkclient.IZkChildListener;
import com.yl.distribute.scheduler.common.utils.DateUtils;
import com.yl.distribute.scheduler.core.zk.ZKHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResouceServerChangeListener {  
	
	private static Log LOG = LogFactory.getLog(ResouceServerChangeListener.class);
	
	//等待选举新的时间（秒）
	private static int ELECT_WAIT_TIME = 3;
    
    public String rmPath = "/rm"; 
   
    private String rmServer = "";
    
    private static ResouceServerChangeListener resouceServerChangeListener = null;      
   
    private ResouceServerChangeListener(String zookeeperServers){
        init(zookeeperServers);
    }
    
    public static synchronized ResouceServerChangeListener getInstance(String zookeeperServers) {
        if(resouceServerChangeListener == null){
            resouceServerChangeListener = new ResouceServerChangeListener(zookeeperServers);
        }
        return resouceServerChangeListener;
    }
    
    public void init(String zookeeperServers) {
    	ZkClient zkClient = ZKHelper.getClient(zookeeperServers);
        List<String> children = zkClient.getChildren(rmPath);
        if(children == null || children.size() == 0){
            throw new RuntimeException("can not get active resource manager server");
        }
        rmServer = children.get(0);
        addNodeChangeListener(zkClient,rmPath);        
    }
    
    public void addNodeChangeListener(final ZkClient zkClient,final String rmPath) {        
        zkClient.subscribeChildChanges(rmPath, new IZkChildListener() {              
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {            
                rmServer = "";
                int i = 1;
                //等待选举新的resourcemanager
                while(i <= ELECT_WAIT_TIME && StringUtils.isBlank(rmServer)) {
                	List<String> children = zkClient.getChildren(rmPath);
                	rmServer = children.get(0);
                	Thread.sleep(1000);
                	i +=1;
                }
                if(StringUtils.isBlank(rmServer)) {
                	LOG.warn("current child is empty, can not get active resource manager at " + DateUtils.getDateAsString(new Date(), DateUtils.dateTimeStr));
                }                     
            }  
        });          
    }

    public String getRmServer() {
        int i = 1;
        try {
            while(i <= ELECT_WAIT_TIME && StringUtils.isBlank(rmServer)) {
            	Thread.sleep(1000);
            	i +=1;
            }	
        }catch(Exception e) {
        	LOG.error(e);
        }
        return rmServer;
    }
    
    public static void main(String[] args){
    }
}