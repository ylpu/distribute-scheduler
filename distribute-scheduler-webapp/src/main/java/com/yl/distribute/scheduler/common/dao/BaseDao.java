package com.yl.distribute.scheduler.common.dao;

import java.io.Serializable;

public interface BaseDao<T,ID extends Serializable> {
	
    int deleteByPrimaryKey(ID id);

    int insertSelective(T record);

    T selectByPrimaryKey(ID id);

    int updateByPrimaryKeySelective(T record);

}
