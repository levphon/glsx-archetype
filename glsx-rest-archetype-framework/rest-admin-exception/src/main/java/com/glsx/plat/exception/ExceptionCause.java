package com.glsx.plat.exception;

import com.glsx.plat.core.web.R;

public interface ExceptionCause<T extends Exception> {

    /**
     * 创建异常
     *
     * @return
     */
    T exception(Object... args);

    R result();

}
