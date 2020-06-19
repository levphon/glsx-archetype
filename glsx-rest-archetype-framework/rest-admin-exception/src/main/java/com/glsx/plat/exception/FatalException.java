package com.glsx.plat.exception;

import javax.persistence.PersistenceException;

/**
 * 可能导致系统故障的异常种类
 */
public enum FatalException {

    NullPointer(NullPointerException.class),
    Persistence(PersistenceException.class);

    private Class<? extends Throwable> exception;

    private FatalException(Class<? extends Throwable> exception) {
        this.exception = exception;
    }

}
