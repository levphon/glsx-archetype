package com.glsx.plat.gateway.exception;

import com.glsx.plat.core.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author payu
 */
@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        //简单返回
        if (!includeStackTrace) return assembleError(request);

        Map<String, Object> map = super.getErrorAttributes(request, includeStackTrace);
        if (getError(request) instanceof GlobalException) {
            GlobalException ex = (GlobalException) getError(request);
            map.put(R.MSG_KEY, ex.getMessage());
            map.put(R.CODE_KEY, ex.getStatus().value());
            return map;
        }
        map.put(R.CODE_KEY, map.getOrDefault("status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        map.put(R.MSG_KEY, "System Error , Check logs!");
        return map;
    }

    private Map<String, Object> assembleError(ServerRequest request) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        if (error instanceof GlobalException) {
            GlobalException ex = (GlobalException) getError(request);
            errorAttributes.put(R.CODE_KEY, ex.getStatus().value());
            errorAttributes.put(R.MSG_KEY, ex.getMessage());
        } else {
            errorAttributes.put(R.CODE_KEY, HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorAttributes.put(R.MSG_KEY, "INTERNAL SERVER ERROR");
        }
        return errorAttributes;
    }

}
