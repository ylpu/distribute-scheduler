package com.yl.distribute.scheduler.common.service.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.transaction.annotation.Transactional;
import com.yl.distribute.scheduler.common.dao.BaseDao;
import com.yl.distribute.scheduler.common.service.BaseService;

@Transactional
public abstract class BaseServiceImpl<T,D extends Serializable>
        implements BaseService<T,D> {

    protected abstract BaseDao<T,  D> getDao();

    protected Class<T> entityClazz;

    @SuppressWarnings("unchecked")
    public BaseServiceImpl() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClazz = (Class<T>) params[0];
    }

    @Transactional(readOnly = true)
    public T findOneById(D Id) {
        return getDao().selectByPrimaryKey(Id);
    }
    
    @Transactional(readOnly = true)
    public void deleteByPrimaryKey(D id) {
    	getDao().deleteByPrimaryKey(id);
    }

    public void insertSelective(T record) {
    	getDao().insertSelective(record);
    }

    public void updateByPrimaryKeySelective(T record) {
    	getDao().updateByPrimaryKeySelective(record);
    }
}