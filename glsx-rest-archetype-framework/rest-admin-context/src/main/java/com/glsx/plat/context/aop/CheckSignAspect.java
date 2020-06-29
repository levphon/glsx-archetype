package com.glsx.plat.context.aop;

import com.glsx.plat.jwt.util.ObjectUtils;
import com.glsx.plat.common.utils.SignUtils;
import com.glsx.plat.core.web.R;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author payu
 * @desc 统一验签切面
 */
@Slf4j
@Aspect
@Configuration
public class CheckSignAspect {

    @Value("${sign.packages:com.glsx}")
    private String scanPackage;

    @Value("${sign.secret:xxx}")
    private String secret;

    // 定义切点Pointcut
    @Pointcut("@annotation(com.glsx.plat.common.annotation.CheckSign)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        String class_name = pjp.getTarget().getClass().getName();
        String method_name = pjp.getSignature().getName();
        String[] paramNames = getFieldsName(class_name, method_name);
        Object[] method_args = pjp.getArgs();

        boolean flag = false;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        //获取所有参数上的注解
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            //int paramIndex = ArrayUtils.indexOf(parameterAnnotations, parameterAnnotation);
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof RequestBody) {
                    flag = true;
                    break;
                }
            }
        }
        SortedMap<String, String> map = new TreeMap<>();
        if (flag) {
            map = logParam(method_args[0]);
        } else {
            map = logParam(paramNames, method_args);
        }
        if (map == null || !map.containsKey("sign")) return R.error("签名校验错误");

        String sign = map.get("sign").toUpperCase();
        map.remove("sign");
        String actualSign = SignUtils.sign(map, secret);
        log.info("_paramSign:{}", sign);
        log.info("actualSign:{}", actualSign);
        if (!sign.equals(actualSign)) {
            return R.error("签名校验错误");
        }

        Object result = pjp.proceed();
        return result;
    }

    /**
     * 使用javassist来获取方法参数名称
     *
     * @param class_name  类名
     * @param method_name 方法名
     * @return
     * @throws Exception
     */
    private String[] getFieldsName(String class_name, String method_name) throws Exception {
        Class<?> clazz = Class.forName(class_name);
        String clazz_name = clazz.getName();
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(clazz);
        pool.insertClassPath(classPath);

        CtClass ctClass = pool.get(clazz_name);
        CtMethod ctMethod = ctClass.getDeclaredMethod(method_name);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            return null;
        }
        String[] paramsArgsName = new String[ctMethod.getParameterTypes().length];
        int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramsArgsName.length; i++) {
            paramsArgsName[i] = attr.variableName(i + pos);
        }
        return paramsArgsName;
    }

    /**
     * 打印方法参数值  基本类型直接打印，非基本类型需要重写toString方法
     *
     * @param paramsArgValue 方法参数值数组
     */
    private SortedMap<String, String> logParam(Object paramsArgValue) {
        Map<String, String> paramMap = (Map<String, String>) ObjectUtils.objectToMap(paramsArgValue);

        SortedMap<String, String> sortedMap = new TreeMap<>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if ("class".equals(entry.getKey())) continue;//跳过转换时多加的类型字段，claims.get("class")为java.lang.Class类型

            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * 打印方法参数值  基本类型直接打印，非基本类型需要重写toString方法
     *
     * @param paramsArgsName  方法参数名数组
     * @param paramsArgsValue 方法参数值数组
     */
    private SortedMap<String, String> logParam(String[] paramsArgsName, Object[] paramsArgsValue) {
        Map<String, String> map = new HashMap();
        if (ArrayUtils.isEmpty(paramsArgsName) || ArrayUtils.isEmpty(paramsArgsValue)) {
            log.info("该方法没有参数");
            return null;
        }
//        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < paramsArgsName.length; i++) {
            //参数名
            String name = paramsArgsName[i];
            //参数值
            Object value = paramsArgsValue[i];
//            if ("sign".equals(name)){
//                continue;
//            }
            if (isPrimite(value.getClass())) {
                map.put(name, String.valueOf(value));
            } else {
                map.put(name, value.toString());
            }
        }
        return new TreeMap<>(map);
    }

    /**
     * 判断是否为基本类型：包括String
     *
     * @param clazz clazz
     * @return true：是;     false：不是
     */
    private boolean isPrimite(Class<?> clazz) {
        if (clazz.isPrimitive() || clazz == String.class) {
            return true;
        } else {
            return false;
        }
    }
}