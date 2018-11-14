package com.yl.scheduler.web.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.Test;

public class DownloadTest {	
	
	@Test
	public void downloadTest() {	
				
        URL url = null;
        HttpURLConnection conn = null;
		try {
			url = new URL("http://www.163.com");
			conn = (java.net.HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
		} catch (Exception e) {
			e.printStackTrace();
		}       
        
		java.io.BufferedReader in = null;
		PrintWriter out = null;
        try{
             in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(),"GBK"));  
             String line;  
             while ((line = in.readLine()) != null) {  
            	 System.out.println(line);  
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
                e.printStackTrace();
            }
        }
	}
}
