package com.glsx.plat.loggin.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.common.enums.OperateType;
import com.glsx.plat.common.enums.RequestSaveMethod;
import com.glsx.plat.common.utils.DateUtils;
import com.glsx.plat.loggin.AbstractLogginStrategy;
import com.glsx.plat.loggin.entity.SysLogEntity;
import com.glsx.plat.web.utils.IpUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * todo 挪到loggin组件里面
 */
@Slf4j
public class LogginTask implements Callable<String> {

    final static GsonBuilder builder = new GsonBuilder();

    final static Gson gson = builder.create();

    final static ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private HttpServletRequest request;
    private String application;
    private Method method;
    private Object[] args;

    private Map<String, Object> userInfo;
    private SysLog sysLogMark;
    private AbstractLogginStrategy strategy;

    public LogginTask(HttpServletRequest request, String application, Method method, Object[] args,
                      Map<String, Object> userInfo,
                      SysLog sysLogMark, AbstractLogginStrategy strategy) {
        this.request = request;
        this.application = application;
        this.method = method;
        this.args = args;
        this.userInfo = userInfo;
        this.sysLogMark = sysLogMark;
        this.strategy = strategy;
    }

    @Override
    public String call() throws Exception {
        // 封装
        SysLogEntity sysLogEntity = wrapSysLogEntity();

        // 存储
        storeSysLogEntity(sysLogEntity);

        return sysLogEntity.getId();
    }

    /**
     * 封装日志对象
     */
    public SysLogEntity wrapSysLogEntity() {
        //创建日志类
        SysLogEntity sysLog = new SysLogEntity();
        sysLog.setApplication(application);
        sysLog.setModule(sysLogMark.module());
        sysLog.setAction(sysLogMark.action().getType());
        sysLog.setRemark(sysLogMark.value());
        Long operatorId = 0L;
        String operator = "";
        if (OperateType.LOGIN.getType().equals(sysLogMark.action().getType())) {
            //登录没参数？？？，不可能
            if (args[0] instanceof String) {
                operator = (String) args[0];
            } else {
                JSONObject loginArg = (JSONObject) JSONObject.toJSON(args[0]);
                if (loginArg.containsKey("account")) {
                    operator = loginArg.getString("account");
                } else if (loginArg.containsKey("username")) {
                    operator = loginArg.getString("username");
                }
            }
        } else {
            try {
                operatorId = Long.valueOf(String.valueOf(userInfo.get("userId")));
            } catch (Exception e) {
                log.error("操作人标识转换异常");
            }
            operator = (String) userInfo.get("account");
            sysLog.setTenant((String) userInfo.get("tenant"));
            sysLog.setBelongOrg((String) userInfo.get("belong"));
        }
        sysLog.setOperatorName(operator);
        sysLog.setOperator(operatorId);

        sysLog.setIp(IpUtils.getIpAddr(request));
        sysLog.setCreatedDate(new Date());

        //保存特定信息
        if (sysLogMark.saveRequest()) {
            try {
                RequestSaveMethod requestSaveMethod = sysLogMark.saveRequestMethod();
                if (requestSaveMethod == RequestSaveMethod.REQUEST) {
                    // TODO: 2020/10/13 从body拿参数
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    sysLog.setRequestData(gson.toJson(parameterMap));
                } else if (requestSaveMethod == RequestSaveMethod.REFLECT) {
                    //使用反射保存请求数据参数
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
                                parseClassRequestData(argsJO, arg, argName);
                            }
                        }
                        sysLog.setRequestData(argsJO.toString());
                    }
                }
            } catch (Exception e) {
                log.error("解析参数出错", e);
            }
        }
        return sysLog;
    }

    /**
     * 存储日志
     *
     * @param sysLog
     */
    public void storeSysLogEntity(SysLogEntity sysLog) {
        log.info("记录日志,保存DB、MQ等 :{}", sysLog);
        strategy.saveLog(sysLog);
    }

    /**
     * 只保存本类的,或原始型数据到请求数据
     *
     * @param jsonObject
     * @param arg
     * @param argName
     */
    private void parseClassRequestData(JSONObject jsonObject, Object arg, String argName) {
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
                parseClassRequestData(jsonArray, array);
            } else if (arg instanceof List) {
                List<Object> list = (List<Object>) arg;
                if (CollectionUtils.isEmpty(list)) {
                    jsonObject.put(argName, arg);
                    return;
                }
                JSONArray jsonArray = new JSONArray();
                jsonObject.put(argName, jsonArray);

                //解析  jsonArray
                parseClassRequestData(jsonArray, list.toArray());
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
    private void parseClassRequestData(JSONArray jsonArray, Object[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return;
        }
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
