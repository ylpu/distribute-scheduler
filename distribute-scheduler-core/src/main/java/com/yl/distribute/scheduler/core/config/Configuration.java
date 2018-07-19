package com.yl.distribute.scheduler.core.config;

import java.io.IOException;
import java.util.Properties;

public class Configuration {
    
    public static Properties getConfig(String propFileName) {
        Properties prop = new Properties();
        try {
            prop.load(Configuration.class.getClassLoader().getResourceAsStream(propFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
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
    
    public static void main(String[] args) {

    }
}
