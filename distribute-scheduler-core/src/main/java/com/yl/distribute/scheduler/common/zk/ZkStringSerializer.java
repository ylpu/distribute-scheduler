package com.yl.distribute.scheduler.common.zk;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class ZkStringSerializer implements ZkSerializer{

    public byte[] serialize(Object data) throws ZkMarshallingError {            
        return String.valueOf(data).getBytes();          
    }

    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        if(bytes == null){
            return null;
        }else{  
           return new String(bytes);            
        }
    }        
}
