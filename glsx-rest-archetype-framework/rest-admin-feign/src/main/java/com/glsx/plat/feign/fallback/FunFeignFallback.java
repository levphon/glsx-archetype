package com.glsx.plat.feign.fallback;


import com.glsx.plat.core.web.R;
import com.glsx.plat.exception.SystemMessage;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class FunFeignFallback<T> implements MethodInterceptor {

    private final Class<T> targetType;
    private final String targetName;
    private final Throwable cause;

    @Nullable
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String errorMessage = cause.getMessage();
        log.error("FunFeignFallback:[{}.{}] serviceId:[{}] message:[{}]", targetType.getName(), method.getName(), targetName, errorMessage);
        // 非 FeignException，直接返回
        if (!(cause instanceof FeignException)) {
            //此处只是示例，具体可以返回带有业务错误数据的对象
            throw cause;
        }
        FeignException exception = (FeignException) cause;
        //此处只是示例，具体可以返回带有业务错误数据的对象
        return R.error(SystemMessage.SERVICE_CALL_FAIL.getCode(), SystemMessage.SERVICE_CALL_FAIL.getMsg() + exception.contentUTF8());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FunFeignFallback<?> that = (FunFeignFallback<?>) o;
        return targetType.equals(that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetType);
    }
}