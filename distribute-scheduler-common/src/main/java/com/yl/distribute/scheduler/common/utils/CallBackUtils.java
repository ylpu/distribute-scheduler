package com.yl.distribute.scheduler.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
public class CallBackUtils {
    
    public static Map<String, Object> CHANNLE_MAP = new ConcurrentHashMap<String, Object>();
    
    public static <T> void putCallback(String requestId, T callback) {
        CHANNLE_MAP.put(requestId, callback);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getCallback(String requestId) {
       return  (T) CHANNLE_MAP.remove(requestId);
    }
}
