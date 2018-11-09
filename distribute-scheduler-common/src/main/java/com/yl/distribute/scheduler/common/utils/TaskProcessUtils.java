package com.yl.distribute.scheduler.common.utils;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

public class TaskProcessUtils {
	
	private static final Log LOG = LogFactory.getLog(TaskProcessUtils.class);
	
	public static Long getLinuxPid(Process process){
        long pid = -1;
        Field field = null;
        Class<?> clazz;
        try {
             clazz = Class.forName("java.lang.UNIXProcess");
             field = clazz.getDeclaredField("pid");
             field.setAccessible(true);
             pid = (Integer) field.get(process);
        } catch (Exception e) {
        	LOG.error(e);
        }
        return pid;
      }
    
     public static Long getWindowsPid(Process process) {
    	 long pid = -1;
         try {
             Field f =process.getClass().getDeclaredField("handle");
             f.setAccessible(true);
             long handl =f.getLong(process);
             Kernel32 kernel = Kernel32.INSTANCE;
             WinNT.HANDLE handle = new WinNT.HANDLE();
             handle.setPointer(Pointer.createConstant(handl));
             int ret =kernel.GetProcessId(handle);
             pid = Long.valueOf(ret);
  
         }catch(Exception e){
        	 LOG.error(e); 
         }
         return pid;
     }
     
     public static void killLinuxProcess(Long pid){

     }
	
     public static void killWindowProcess(Long pid){
         try {
              System.out.println("kill process with id " + pid);
              String cmd =getKillProcessTreeCmd(pid);
              Runtime rt =Runtime.getRuntime();
              Process killPrcess = rt.exec(cmd); 
              killPrcess.waitFor();
              killPrcess.destroy();
          }catch(Exception e){
        	  LOG.error(e);
          }
     }

     private static String getKillProcessTreeCmd(Long Pid){
         String result = "";
         if(Pid !=null)
            result ="c:/windows/system32/cmd.exe /c taskkill /PID "+ Pid + " /F /T ";
            return result;
     }
	    
//     public static void main(String[] args) throws Exception {
//         String command = "cmd /c echo abc";
//         Runtime rt = Runtime.getRuntime();
//         Process process = rt.exec(command);
//         IOUtils.writeFile(process.getInputStream(),"d:/output/txt/stdout.txt");
//         IOUtils.writeFile(process.getErrorStream(),"d:/output/txt/stderror.txt");
//         process.waitFor();
//         killWindowProcess(getWindowsPid(process));	
//     }
}
