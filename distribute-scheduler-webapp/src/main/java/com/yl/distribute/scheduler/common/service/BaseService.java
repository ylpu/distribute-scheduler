package com.yl.distribute.scheduler.common.service;

import java.io.Serializable;

public interface BaseService<T,D extends Serializable> {
	
	public T findOneById(D Id);   

    public void deleteByPrimaryKey(D id);

    public void insertSelective(T record);

    public void updateByPrimaryKeySelective(T record);
}
