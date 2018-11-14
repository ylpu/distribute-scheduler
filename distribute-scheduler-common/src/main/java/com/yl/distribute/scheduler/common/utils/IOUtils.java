package com.yl.distribute.scheduler.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

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
	       File file = new File(fileName);
	       String parentPath = file.getParent();
	       File parentfile = new File(parentPath);
	       if (!parentfile.exists()) {
	    	   parentfile.mkdirs();
	       }
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
   
   public static void writeFile(String content, String filePath) {	   
	   FileOutputStream fop = null;
	   File file = null ;
	   try {
	       file = new File(filePath);
	       String parentPath = file.getParent();
	       File parentfile = new File(parentPath);
	       if (!parentfile.exists()) {
	    	   parentfile.mkdirs();
	       }
	       fop = new FileOutputStream(file);
	       byte[] contentInBytes = content.getBytes();
	       fop.write(contentInBytes);
	   } catch (IOException e) {
		   LOG.error(e);
	   } finally {
	        try {
	            if (fop != null) {
	                fop.close();
	            }
	        } catch (IOException e) {
         	   LOG.error(e);
	        }
	   }	  
   }
   
   public static String readFile(String fileName) {  
	   StringBuilder sb = new StringBuilder();
       File file = new File(fileName);
       if(file.exists()) {
           BufferedReader reader = null;  
           try {  
               reader = new BufferedReader(new FileReader(file));  
               String tempString = null;  
               while ((tempString = reader.readLine()) != null) {  
            	   sb.append(tempString);
               }  
           } catch (IOException e) {  
        	   LOG.error(e);
           } finally {  
               if (reader != null) {  
                   try {  
                       reader.close();  
                   } catch (IOException e) {  
                	   LOG.error(e);
                   }  
               }  
           }  
       }else {
    	   LOG.warn("file " + fileName + " does not exists");
       }
       return sb.toString();
   } 
   
   public static void removeFile(String fileName) {  
       File file = new File(fileName);
       if(file.exists()) {
    	   file.delete();    	   
       }
   } 
   
   public static void downloadByUrl(HttpServletResponse response,String fileUrl) {
       URL url = null;
       HttpURLConnection conn = null;
		try {
			url = new URL(fileUrl);
			conn = (java.net.HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
		} catch (Exception e) {
			LOG.error(e);
		}       
		java.io.BufferedReader in = null;
		PrintWriter out = null;
        try{
        	out = response.getWriter();
            in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(),"UTF-8"));  
            String line;  
            while ((line = in.readLine()) != null) {  
           	 out.write(line);
           	 out.flush();
            }
        } catch (Exception e) {
       	     e.printStackTrace();
        }finally{
           try {
               if (in != null) {
                   in.close();
               }
               if (out != null) {
               	out.close();
               }
            } catch (IOException e) {
        	   LOG.error(e);
           }
       }
	}   
}