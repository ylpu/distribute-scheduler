package com.yl.distribute.scheduler.common.utils;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import com.sun.management.OperatingSystemMXBean;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.constants.GlobalConstants;

@SuppressWarnings("restriction")
public class MetricsUtils {
    
    public static void main(String[] args)
    {
        System.out.println(getFreeMemInfo());
        System.out.println(getTotalMemInfo());
        System.out.println(getAvailiableProcessors());
        System.out.println(getHostName());
        System.out.println(getHostIpAddress());
        JobRequest jobConf = new JobRequest();
        jobConf.setCommand("java -jar abc.jar");
        System.out.println(getTaskMemory(jobConf));
    }  

    
    public static long getFreeMemInfo()
    {
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return mem.getFreePhysicalMemorySize() / 1024 / 1024;
    }
    
    public static long getTotalMemInfo()
    {
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return mem.getTotalPhysicalMemorySize() / 1024 / 1024;
    }
    
    public static int getAvailiableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
    
    public static double getCpuLoad() {
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        BigDecimal bg = new BigDecimal(mem.getSystemCpuLoad() * 100);
        double percentCpuLoad = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return percentCpuLoad;
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
    
    public static long getTaskMemory(JobRequest jobConf) {
        long memory = 0;
        if(StringUtils.isNotBlank(jobConf.getCommand())) {
           if(jobConf.getCommand().indexOf("-Xmx") > 0) {
              memory = extractMemoryInfo(jobConf.getCommand(),"-Xmx");
              if(memory > 0 ) {
                return memory;
              }
           }
         }   
       return GlobalConstants.DEFAUTL_MEMEORY;
    }
    
    private static long extractMemoryInfo(String command,String key) {
        List<String> parameters = Arrays.asList(command.split("\\s+"));
        for(String parameter : parameters) {
            if(parameter.startsWith(key)) {
                return NumberUtils.toInt(parameter.substring(key.length(),parameter.length()-1));
            }
        }
        return 0;
    }
}