package com.glsx.plat.context.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.common.enums.RequestSaveMethod;
import com.glsx.plat.common.utils.DateUtils;
import com.glsx.plat.common.utils.StringUtils;
import com.glsx.plat.web.utils.IpUtils;
import com.glsx.plat.core.constant.BasicConstants;
import com.glsx.plat.core.entity.SysLogEntity;
import com.glsx.plat.jwt.base.BaseJwtUser;
import com.glsx.plat.jwt.util.JwtUtils;
import com.glsx.plat.redis.service.GainIdService;
import com.glsx.plat.web.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author payu
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {

    @Resource
    private GainIdService gainIdService;

    @Resource
    private JwtUtils jwtUtils;

    private final static String LOG_REQ_ID = "REQ_ID";

    /**
     * 换行符
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

//    @Resource
//    private MongoTemplate mongoTemplate;

//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;

    @Pointcut("@annotation(com.glsx.plat.common.annotation.SysLog)")
    public void logPointCut() {
    }

    ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @Before("logPointCut()")
    public void saveLog(JoinPoint joinPoint) {
        String traceId = gainIdService.gainId(LOG_REQ_ID);
        MDC.put(LOG_REQ_ID, traceId);

        saveSysLog(joinPoint);
    }

    /**
     * 打印方法执行时间
     */
    @Around("logPointCut()")
    public Object timeAround(ProceedingJoinPoint jp) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法,执行失败后会抛出异常,也就不会打印执行时间
        Object result = jp.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Object target = jp.getTarget();
        Method method = methodSignature.getMethod();
        //获取注解上的文字
        SysLog sysLog = method.getAnnotation(SysLog.class);
        String methodName = methodSignature.getName();
        if (sysLog.printSpendTime()) {
            log.info(target.getClass().getName() + "." + methodName + " 执行耗时:" + DurationFormatUtils.formatDuration(time, "HH:mm:ss.S") + "; 精确时间为 :" + time + " ms");
        }
        return result;
    }

    /**
     * 方法之后调用
     *
     * @param joinPoint
     * @param returnValue 方法返回值
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        log.info("Returning Args : {}", JSON.toJSONString(returnValue));
        // 处理完请求，返回内容

        MDC.clear();
    }

    /**
     * 供后台和基础服务调用系统日志保存
     *
     * @param joinPoint
     */
    public void saveSysLog(JoinPoint joinPoint) {

        //获取方法信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object target = joinPoint.getTarget();

        // 日志标识
        SysLog sysLogMark = method.getAnnotation(SysLog.class);

        // 开始打印请求日志
        // 获取 @WebLog 注解的描述信息
        String methodDescription = sysLogMark.value();

        HttpServletRequest request = SessionUtils.request();

        // 打印请求相关参数
        log.info("========================================== Start ==========================================");
        // 打印请求 url
        log.info("URL            : {}", request.getRequestURL().toString());
        // 打印描述信息
        log.info("Description    : {}", methodDescription);
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", methodSignature.getDeclaringTypeName(), methodSignature.getName());
        // 打印请求的 IP
        log.info("IP             : {}", request.getRemoteAddr());
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));

        String token = request.getHeader(BasicConstants.REQUEST_HEADERS_TOKEN);
        if (StringUtils.isNotEmpty(token)) {
            // TODO: 2020/5/27 jwt解析token
            Map<String, Object> claimMap = jwtUtils.parseClaim(BaseJwtUser.class, token);
            log.info(claimMap.toString());
        }

        if (sysLogMark.saveLog()) {
            String modul = target.getClass().getName() + "." + methodSignature.getName();
            //创建日志类
            SysLogEntity sysLog = new SysLogEntity();
            sysLog.setModul(modul);

            String remortIP = IpUtils.getIpAddr(request);
            sysLog.setIp(remortIP);
            try {
                //保存特定信息
                if (sysLogMark.saveRequest()) {
                    RequestSaveMethod requestSaveMethod = sysLogMark.saveRequestMethod();
                    if (requestSaveMethod == RequestSaveMethod.REQUEST) {
                        Map<String, String[]> parameterMap = request.getParameterMap();
                        sysLog.setRequestData(JSONObject.toJSONString(parameterMap));
                    } else if (requestSaveMethod == RequestSaveMethod.REFLECT) {
                        //使用反射保存请求数据参数
                        Object[] args = joinPoint.getArgs();
                        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
                        if (ArrayUtils.isNotEmpty(args)) {
                            JSONObject argsJO = new JSONObject();
                            for (int i = 0; i < args.length; i++) {
                                Object arg = args[i];
                                String argName = parameterNames[i];
                                if (arg == null) {//空参记录
                                    argsJO.put(argName, null);
                                } else {
                                    //只取项目中的类或原始型和基本类型
                                    saveClassRequestData(argsJO, arg, argName);
                                }
                            }
                            sysLog.setRequestData(argsJO.toJSONString());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("保存日志出错", e);
            } finally {
                log.info("记录日志,保存DB、MQ等 TODO :{}", sysLog);

//                sysLogMapper.insert(sysLog);
//                mongoTemplate.insert(sysLog);
//                kafkaTemplate.send(sysLog);
            }
        }

    }


    /**
     * 只保存本类的,或原始型数据到请求数据
     *
     * @param jsonObject
     * @param arg
     * @param argName
     */
    private void saveClassRequestData(JSONObject jsonObject, Object arg, String argName) {
        Class<?> clazz = arg.getClass();
        boolean primitiveOrWrapper = org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper(clazz);
        if (primitiveOrWrapper || clazz == String.class) {
            jsonObject.put(argName, arg);
        } else if (clazz == Date.class) {
            Date date = (Date) arg;
            jsonObject.put(argName, DateUtils.formatNormal(date));
        } else if (clazz.isArray() || (arg instanceof List) || (arg instanceof Map)) {
            if (clazz.isArray()) {
                Object[] array = (Object[]) arg;
                if (ArrayUtils.isEmpty(array)) {
                    jsonObject.put(argName, arg);
                    return;
                }
                JSONArray jsonArray = new JSONArray();
                jsonObject.put(argName, jsonArray);

                //解析  jsonArray
                saveClassRequestData(jsonArray, array);
            } else if (arg instanceof List) {
                List<Object> list = (List<Object>) arg;
                if (CollectionUtils.isEmpty(list)) {
                    jsonObject.put(argName, arg);
                    return;
                }
                JSONArray jsonArray = new JSONArray();
                jsonObject.put(argName, jsonArray);

                //解析  jsonArray
                saveClassRequestData(jsonArray, list.toArray());
            } else if (arg instanceof Map) {
                // map 不处理了,直接放进去
                jsonObject.put(argName, arg);
            }
        }
    }

    /**
     * 保存数组数据
     *
     * @param jsonArray
     * @param array
     */
    private void saveClassRequestData(JSONArray jsonArray, Object[] array) {
        if (ArrayUtils.isEmpty(array)) return;
        for (Object o : array) {
            if (o == null) {
                jsonArray.add(null);
                continue;
            }

            Class<?> clazz = o.getClass();
            boolean primitiveOrWrapper = org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper(clazz);
            if (primitiveOrWrapper || clazz == String.class) {
                jsonArray.add(o);
            } else if (clazz == Date.class) {
                Date date = (Date) o;
                jsonArray.add(DateUtils.formatNormal(date));
            }
        }
    }

}
