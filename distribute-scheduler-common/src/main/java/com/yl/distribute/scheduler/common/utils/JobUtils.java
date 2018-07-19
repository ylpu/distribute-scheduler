package com.yl.distribute.scheduler.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JobUtils {
    
    private static String DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    
    private JobUtils() {
        
    }
    
    public static String getJobId(String requestId) {
        return requestId + "_" + new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date());
    }
}