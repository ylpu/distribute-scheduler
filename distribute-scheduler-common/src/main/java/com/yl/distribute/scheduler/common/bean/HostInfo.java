package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HostInfo implements Serializable,Comparable<HostInfo>{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String ipAddress;
    
    private String hostName;
    
    private int cores;
    
    private long memory; 

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }    

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public int compareTo(HostInfo o) {
       if(o!= null) {
           HostInfo hostInfo =(HostInfo)o;
           if(getMemory() > hostInfo.getMemory()) {
               return -1;
           }else if(getMemory() == hostInfo.getMemory()) {
               if(getCores() > hostInfo.getCores()) {
                   return -1;
               }else {
                   return 1;
               }
           }else {
               return 1;
           }
       }
       throw new RuntimeException("对象为空");
    }
    
    public static void main(String[] args) {
        List<HostInfo> list = new ArrayList<HostInfo>();
        
        HostInfo hostInfo = new HostInfo();
        hostInfo = new HostInfo();
        hostInfo.setMemory(1200);
        hostInfo.setCores(7);
        list.add(hostInfo);
        
        HostInfo hostInfo1 = new HostInfo();
        hostInfo1 = new HostInfo();
        hostInfo1.setMemory(1000);
        hostInfo1.setCores(8);        
        list.add(hostInfo1);
        
        HostInfo hostInfo2 = new HostInfo();
        hostInfo2 = new HostInfo();
        hostInfo2.setMemory(1500);
        hostInfo2.setCores(8);        
        list.add(hostInfo2);
        
        Collections.sort(list);
        
        list.remove(hostInfo);
        
        System.out.println(list);
    }    
}