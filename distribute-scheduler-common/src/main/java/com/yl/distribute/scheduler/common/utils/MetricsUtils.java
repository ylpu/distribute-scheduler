package com.yl.distribute.scheduler.common.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import com.sun.management.OperatingSystemMXBean;
import com.yl.distribute.scheduler.common.constants.GlobalConstants;

public class MetricsUtils {
    
    public static void main(String[] args)
    {
        System.out.println(getMemInfo());
        System.out.println(getAvailiableProcessors());
        getHostName();
        getHostIpAddress();
    }  

    
    @SuppressWarnings("restriction")
    public static long getMemInfo()
    {
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return mem.getFreePhysicalMemorySize() / 1024 / 1024;
    }
    
    public static int getAvailiableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
    
    public static String getHostName() {
        InetAddress addr=null;  
        String hostName="";  
        try{
            addr = InetAddress.getLocalHost();
            hostName = addr.getHostName().toString(); //获得机器名称  
        }catch(Exception e){  
            throw new RuntimeException("can not find host name"); 
        }  
        return hostName;
    }
    
    public static String getHostIpAddress() {
        InetAddress addr=null;  
        String ip="";  
        try{
            addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress().toString(); //获得机器IP　　  
        }catch(Exception e){  
            throw new RuntimeException("can not find ip address");  
        } 
        return ip;
    }
    
    public static long getTaskMemory(String args) {
        if(args != null && args.length() > 0) {
            List<String> parameters = Arrays.asList(args.split("\\s+"));
            for(String parameter : parameters) {
                if(parameter.startsWith("-Xmx")) {
                    return NumberUtils.toInt(parameter.substring(4,parameter.length()-1));
                }
            }
            return GlobalConstants.DEFAUTL_MEMEORY;
        }
        return GlobalConstants.DEFAUTL_MEMEORY;
    }
}