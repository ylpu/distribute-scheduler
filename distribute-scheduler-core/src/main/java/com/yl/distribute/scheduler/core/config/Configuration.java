package com.yl.distribute.scheduler.core.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Configuration {
	
    private static Log LOG = LogFactory.getLog(Configuration.class);
	
    private static Map<String,Properties> configMap = new HashMap<String,Properties>();
    
    public static Properties getConfig(String propFileName) {
    	Properties config = configMap.get(propFileName);
    	if(config == null){
            Properties prop = new Properties();
            try {
                prop.load(Configuration.class.getClassLoader().getResourceAsStream(propFileName));
            } catch (IOException e) {
            	LOG.error(e);
            }
            config = prop;
            configMap.put(propFileName, prop);
    	}
        return config;
    }
    
    public static int getInt(Properties prop,String key) {
        return Integer.parseInt(prop.getProperty(key));
       
    }
    
    public static String getString(Properties prop,String key) {
        return prop.getProperty(key);
       
    }
    
    public static Boolean getBoolean(Properties prop,String key) {
        return Boolean.valueOf(prop.getProperty(key));
       
    }
    
    public static Double getDouble(Properties prop,String key) {
        return Double.valueOf(prop.getProperty(key));       
    }
    
    public static Long getLong(Properties prop,String key) {
        return Long.valueOf(prop.getProperty(key));       
    }
}
