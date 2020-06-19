package com.glsx.plat.feign.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.glsx.plat.exception.BusinessException;
import com.glsx.plat.exception.ExceptionInfo;
import com.glsx.plat.exception.ServiceException;
import com.glsx.plat.exception.SystemMessage;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * feign 服务的异常解析器
 *
 * @author payu
 */
@Slf4j
@Configuration
public class ExceptionErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.body() != null) {
                ExceptionInfo exceptionInfo = JSON.parseObject(Util.toString(response.body().asReader()), new TypeReference<ExceptionInfo>() {
                });
                if (exceptionInfo.getException() == null)
                    return ServiceException.create(SystemMessage.MICROSERVICE_CALL_FAIL.getMsg(), "Feign服务调用出错>>>" + JSON.toJSONString(exceptionInfo));

                Class clazz = Class.forName(exceptionInfo.getException());
                Exception exception = (Exception) clazz.getDeclaredConstructor(String.class).newInstance(exceptionInfo.getMessage());
                if (exception instanceof BusinessException) {
                    log.error("业务异常：" + exception.getMessage());
                }
                return exception;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FeignException.errorStatus(methodKey, response);
    }

}
