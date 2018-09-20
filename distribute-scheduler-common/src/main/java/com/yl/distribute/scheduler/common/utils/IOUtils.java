package com.yl.distribute.scheduler.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class IOUtils {
    
    private static final Log LOG = LogFactory.getLog(IOUtils.class);
    
    private IOUtils() {
        
    }
    
    public static void writeOuput(InputStream is,String fileName){        
        new Thread(new Runnable() {
          public void run() {
        	  writeFile(is,fileName);
        }
      }).start();
    }
    
   public static void writeFile(InputStream is,String fileName) {
	   BufferedWriter bw = null;
       try {
           bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream (fileName,true),"GBK"));
           BufferedReader brError = new BufferedReader(new InputStreamReader(is, "GBK"));
           String errline = null;
           while ((errline = brError.readLine()) != null) {
               bw.write(errline);
               bw.flush();
           }
       } catch (IOException e) {
           LOG.error(e);
           throw new RuntimeException(e);
       } finally {            
           try {
               if(bw != null) {
                  bw.close();
               }
               if(is != null) {
            	   is.close();
               }
           } catch (IOException e) {
               LOG.error(e);
               throw new RuntimeException(e);
           }
       }        
   }
}