package com.glsx.plat.exception;

import com.glsx.plat.core.web.R;
import com.netflix.client.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author payu
 * @desc 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${project.package.prefix:com.glsx}")
    protected String packagePrefix;

    @Value("${spring.application.name}")
    protected String moduleName;

    /**
     * 业务异常处理
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public R businessExceptionHandler(HttpServletRequest req, Exception e) {
        BusinessException businessException = (BusinessException) e;

        StackTraceElement[] stackTrace = e.getStackTrace();
        List<StackTraceElement> localStackTrack = new ArrayList<>();
        StringBuffer showMessage = new StringBuffer();
        if (ArrayUtils.isNotEmpty(stackTrace)) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                String className = stackTraceElement.getClassName();
                int lineNumber = stackTraceElement.getLineNumber();
                if (className.startsWith(packagePrefix)) {
                    localStackTrack.add(stackTraceElement);
                    showMessage.append(className + "(" + lineNumber + ")\n");
                }
            }
            log.error("业务异常:" + e.getMessage() + "\n" + showMessage);
        } else {
            log.error("业务异常,没有调用栈 " + e.getMessage());
        }
        saveException(businessException, ExceptionLevel.business);
        return businessException.getResultEntity();
    }

    /**
     * 网络或资源找不到
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = IOException.class)
    public R ioException(HttpServletRequest req, Exception e) {
        log.error("网络连接错误", e);
        saveException(e, ExceptionLevel.normal);
        return SystemMessage.NETWORK_ERROR.result();
    }

    /**
     * 绑定参数异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public R bindException(BindException ex) {
        // ex.getFieldError():随机返回一个对象属性的异常信息。如果要一次性返回所有对象属性异常信息，则调用ex.getAllErrors()
        FieldError fieldError = ex.getFieldError();
        StringBuilder sb = new StringBuilder();
        assert fieldError != null;
        sb.append(fieldError.getField())
                .append("=[").append(fieldError.getRejectedValue()).append("]")
                .append(fieldError.getDefaultMessage());
        log.error("参数错误：{}", sb.toString());
        saveException(ex, ExceptionLevel.business);
        return R.error(SystemMessage.ARGS_NULL.getCode(), sb.toString());
    }

    @ExceptionHandler(value = PersistenceException.class)
    public R persistenceException(PersistenceException e, HttpServletRequest request) {
        Throwable cause = e.getCause();
        if (cause instanceof BusinessException) {
            return businessExceptionHandler(request, e);
        }
        log.error("数据存储出错", e);
        saveException(e, ExceptionLevel.fatal);
        return SystemMessage.DATA_PERSISTENCE_ERROR.result();
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public R duplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        Throwable cause = e.getCause();
        if (cause instanceof BusinessException) {
            return businessExceptionHandler(request, e);
        }
        log.error("数据存储出错", e);
        saveException(e, ExceptionLevel.fatal);
        return SystemMessage.DATA_PERSISTENCE_ERROR.result();
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public R methodNotSupport(HttpRequestMethodNotSupportedException e) {
        log.error("Http请求异常", e);
        saveException(e, ExceptionLevel.fatal);
        return SystemMessage.ACCESS_DENIED.result(e.getMessage());
    }

    @ExceptionHandler(value = ValidateException.class)
    public R validateException(ValidateException e) {
        log.error("数据校验异常", e);
        saveException(e, ExceptionLevel.normal);
        return R.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public R handleServiceException(ConstraintViolationException e) {
        log.error("参数验证失败", e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder buf = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            buf.append(violation.getMessage()).append(",");
        }
        saveException(e, ExceptionLevel.fatal);
        return R.error(HttpStatus.BAD_REQUEST.value(), buf.deleteCharAt(buf.length() - 1).toString());
    }

    /**
     * 处理@Validated参数校验失败异常
     *
     * @param e 异常类
     * @return 响应
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            sb.append(error.getDefaultMessage()).append(",");
        }
        log.error("参数错误：{}", sb);
        saveException(e, ExceptionLevel.fatal);
        return R.error(HttpStatus.BAD_REQUEST.value(), sb.length() > 0 ? sb.deleteCharAt(sb.length() - 1).toString() : "参数异常！");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String name = ex.getParameterName();
        String type = ex.getParameterType();
        log.error("请求{} 参数[{}]类型[{}] 错误信息：{}", uri, name, type, ex.getMessage());
        return R.error(HttpStatus.BAD_REQUEST.value(), name + " 参数异常！");
    }

    @ExceptionHandler(ServiceException.class)
    public R handleServiceException(ServiceException e) {
        saveException(e, ExceptionLevel.fatal);
        return R.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(ClientException.class)
    public R handleClientException(ClientException e) {
        saveException(e, ExceptionLevel.fatal);
        return R.error(e.getErrorCode(), e.getErrorMessage());
    }

    /**
     * 未识别异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public R otherException(Exception e) {
        log.error("后台服务异常", e);
        saveException(e, ExceptionLevel.fatal);
        return SystemMessage.SERVICE_CALL_FAIL.result();
    }

    /**
     * 异常保存，换成elk分析日志文件
     *
     * @param e
     * @param level
     */
    protected void saveException(Exception e, ExceptionLevel level) {
        //清除日志
        MDC.clear();

        // TODO: 2020/10/15 异常信息入库
    }

}
