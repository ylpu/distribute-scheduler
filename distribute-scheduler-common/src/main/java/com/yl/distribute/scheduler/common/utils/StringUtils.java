package com.yl.distribute.scheduler.common.utils;

import java.net.URL;

public class StringUtils {

    private StringUtils() {
         
    }
    
    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');        
        if (-1 == pos) {
            return fileUrl;
        }        
        return fileUrl.substring(5, pos);
    }        

    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }        
        return name;
    }

    public static String trimURI(String uri) {
        String trimmed = uri.substring(1);
        int splashIndex = trimmed.indexOf('/');        
        return trimmed.substring(splashIndex);
    }
}