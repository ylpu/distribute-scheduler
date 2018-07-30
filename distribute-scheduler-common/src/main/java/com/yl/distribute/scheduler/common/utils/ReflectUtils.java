package com.yl.distribute.scheduler.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ReflectUtils {
    
    private ReflectUtils() {
        
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        if(Modifier.isPrivate(field.getModifiers())) {
            field.setAccessible(true);
        }   
        return (T) field.get(obj);
    }
}
