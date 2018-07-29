package com.yl.distribute.scheduler.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskResponse;

public abstract class KryoFactory {


    private final static KryoFactory threadFactory = new ThreadLocalKryoFactory();
   

    protected KryoFactory() {
        
    }

    public static KryoFactory getDefaultFactory() {
        return threadFactory;
    }


    protected Kryo createKryo() {      
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.register(JobConf.class);
        kryo.register(TaskResponse.class);
        return kryo;
    }
}
