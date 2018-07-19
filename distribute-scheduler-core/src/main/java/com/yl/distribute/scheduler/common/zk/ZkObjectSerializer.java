package com.yl.distribute.scheduler.common.zk;

import java.io.Serializable;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang3.SerializationUtils;

public class ZkObjectSerializer implements ZkSerializer{

    public byte[] serialize(Object data) throws ZkMarshallingError {            
        return SerializationUtils.serialize((Serializable) data);          
    }

    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return SerializationUtils.deserialize(bytes);
    }        
}
