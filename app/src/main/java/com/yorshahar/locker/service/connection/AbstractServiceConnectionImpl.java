package com.yorshahar.locker.service.connection;

import android.content.ServiceConnection;

/**
 * Created by yorshahar on 8/13/15.
 */
public abstract class AbstractServiceConnectionImpl implements ServiceConnection {
    private Class clazz;

    protected AbstractServiceConnectionImpl(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getServiceName() {
        return clazz.getName();
    }

}
