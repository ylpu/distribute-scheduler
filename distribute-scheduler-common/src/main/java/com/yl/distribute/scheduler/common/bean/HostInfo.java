package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

public class HostInfo implements Serializable,Comparable<HostInfo>{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * ip address of current host
     */
    
    private String ip;
    
    /**
     * host name of current host
     */
    
    private String hostName;
    
    /**
     * total cores of current host
     */
    
    private int totalCores;
    
    /**
     * total memory of current host
     */
    
    private long totalMemory; 
    
    /**
     * available cores of current host
     */
    
    private int availableCores;
    
    /**
     * available memory of current host
     */
    
    private long availableMemory;   
    
    /**
     * cpu load
     */
    private Double cpuLoad;

    public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getTotalCores() {
		return totalCores;
	}

	public void setTotalCores(int totalCores) {
		this.totalCores = totalCores;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}   

    public int getAvailableCores() {
		return availableCores;
	}

	public void setAvailableCores(int availableCores) {
		this.availableCores = availableCores;
	}

	public long getAvailableMemory() {
		return availableMemory;
	}

	public void setAvailableMemory(long availableMemory) {
		this.availableMemory = availableMemory;
	}

	public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Double getCpuLoad() {
		return cpuLoad;
	}

	public void setCpuLoad(Double cpuLoad) {
		this.cpuLoad = cpuLoad;
	}

	@Override
    public int compareTo(HostInfo o) {
       if(o!= null) {
           HostInfo hostInfo =(HostInfo)o;
           if(getAvailableMemory() > hostInfo.getAvailableMemory()) {
               return -1;
           }else if(getAvailableMemory() == hostInfo.getAvailableMemory()) {
               if(getAvailableCores() >= hostInfo.getAvailableCores()) {
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
   
    }    
}