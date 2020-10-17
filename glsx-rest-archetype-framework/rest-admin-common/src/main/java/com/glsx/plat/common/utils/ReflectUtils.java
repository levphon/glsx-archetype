package com.glsx.plat.common.utils;

import com.glsx.plat.common.annotation.ReadOnly;
import com.glsx.plat.common.model.ParameterAnnotationHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReflectUtils {

    public static <T> List<T> castEntity(List<Object[]> list, Class<T> clazz) throws Exception {
        List<T> returnList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) return returnList;

        List<Class<?>> allFieldsList = getAllFieldsList(clazz);
        Class[] mod = allFieldsList.toArray(new Class[list.get(0).length]);
        for (Object[] co : list) {
            Constructor<T> constructor = clazz.getConstructor(mod);
            returnList.add(constructor.newInstance(co));
        }
        return returnList;
    }

    public static List<Class<?>> getAllFieldsList(final Class<?> cls) {
        Validate.isTrue(cls != null, "The class must not be null");
        final List<Class<?>> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (final Field field : declaredFields) {
                allFields.add(field.getType());
            }
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    public static <T, E> T getValueOfMethodAnnotation(Method method, Class<Annotation> annotation) {
        ParameterAnnotationHolder parameterAnnotationHolder = getAnnotationFromMethodParameters(method, annotation);
        Integer type = parameterAnnotationHolder.getType();
        if (type == 1) {
            String parameterName = parameterAnnotationHolder.getParameterName();

        } else if (type == 2) {
            return null;
        }
        return null;
    }

    /**
     * 从方法参数前&参数实体字段中获得注解字段值
     *
     * @param annotation
     * @param <T>
     * @return
     */
    public static <T> ParameterAnnotationHolder getAnnotationFromMethodParameters(Method method, Class<Annotation> annotation) {

        ParameterAnnotationHolder parameterAnnotationHolder = getAnnotationBeforeMethodParameters(method, annotation);

        return parameterAnnotationHolder == null ? getAnnotationFromMethodParametersEntityField(method, annotation) : parameterAnnotationHolder;
    }

    public static void main(String[] args) {
        ReflectUtils.testInParameter(new ParameterAnnotationHolder().setType(1));
    }

    @ReadOnly
    public static void testInParameter(ParameterAnnotationHolder inPara) {

    }

    /**
     * 从方法参数实体字段中获得注解（包括父类）
     *
     * @param method
     * @param annotation
     * @return
     */
    public static ParameterAnnotationHolder getAnnotationFromMethodParametersEntityField(Method method, Class<Annotation> annotation) {

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Class<? extends Parameter> parameterClass = parameter.getClass();
            Field[] fields = parameterClass.getDeclaredFields();

            for (Field field : fields) {
                if (field.getDeclaredAnnotation(annotation) != null) {
                    field.setAccessible(true);
                    return new ParameterAnnotationHolder().setType(2).setParameterName(parameter.getName()).setFieldName(field.getName());
                }
            }

            Class<?> superClazz = parameterClass.getSuperclass();
            if (superClazz != null) {
                Field[] superFields = superClazz.getDeclaredFields();
                for (Field superField : superFields) {
                    if (superField.getDeclaredAnnotation(annotation) != null) {
                        superField.setAccessible(true);
                        return new ParameterAnnotationHolder().setType(2).setParameterName(parameter.getName()).setFieldName(superField.getName());
                    }
                }
            }
        }

        return null;
    }

    /**
     * 从方法参数泛型实体字段中获得注解（包括父类）
     *
     * @param method
     * @param annotation
     * @return
     */
    public static ParameterAnnotationHolder getAnnotationFromMethodParametersGenericField(Method method, Class<Annotation> annotation) {

        return null;
    }

    /**
     * 从方法参数前获得注解
     * list<string> or string
     *
     * @param method
     * @param annotation
     * @return
     */
    public static ParameterAnnotationHolder getAnnotationBeforeMethodParameters(Method method, Class<Annotation> annotation) {
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (null != parameter.getDeclaredAnnotation(annotation)) {
//                parameter.getType()
                return new ParameterAnnotationHolder().setType(1).setParameterName(parameter.getName());
            }
        }
        return null;
    }

    /**
     * 从方法参数前获得泛型注解（包括父类）
     *
     * @param method
     * @param annotation
     * @return
     */
    public static ParameterAnnotationHolder getAnnotationBeforeMethodGenericParameters(Method method, Class<Annotation> annotation) {
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (null != parameter.getDeclaredAnnotation(annotation)) {

                return new ParameterAnnotationHolder().setType(1).setParameterName(parameter.getName());
            }
        }
        return null;
    }


    public static void getObjectValue(Object object) throws Exception {
        if (object != null) {
            Class<?> clz = object.getClass();
            Field[] fields = clz.getDeclaredFields();

            for (Field field : fields) {
                if (field.getGenericType().toString().equals(
                        "class java.lang.String")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名。 拿到该属性的gettet方法
                    Method m = object.getClass().getMethod(
                            "get" + getMethodName(field.getName()));

                    // 调用getter方法获取属性值
                    String val = (String) m.invoke(object);
                    if (val != null) {
                        System.out.println("String type:" + val);
                    }
                }

                // 如果类型是Integer
                if (field.getGenericType().toString().equals(
                        "class java.lang.Integer")) {
                    Method m = (Method) object.getClass().getMethod(
                            "get" + getMethodName(field.getName()));
                    Integer val = (Integer) m.invoke(object);
                    if (val != null) {
                        System.out.println("Integer type:" + val);
                    }

                }

                // 如果类型是Double
                if (field.getGenericType().toString().equals(
                        "class java.lang.Double")) {
                    Method m = (Method) object.getClass().getMethod(
                            "get" + getMethodName(field.getName()));
                    Double val = (Double) m.invoke(object);
                    if (val != null) {
                        System.out.println("Double type:" + val);
                    }

                }

                // 如果类型是Boolean 是封装类
                if (field.getGenericType().toString().equals(
                        "class java.lang.Boolean")) {
                    Method m = (Method) object.getClass().getMethod(
                            field.getName());
                    Boolean val = (Boolean) m.invoke(object);
                    if (val != null) {
                        System.out.println("Boolean type:" + val);
                    }

                }

                // 如果类型是boolean 基本数据类型不一样 这里有点说名如果定义名是 isXXX的 那就全都是isXXX的
                // 反射找不到getter的具体名
                if (field.getGenericType().toString().equals("boolean")) {
                    Method m = (Method) object.getClass().getMethod(
                            field.getName());
                    Boolean val = (Boolean) m.invoke(object);
                    if (val != null) {
                        System.out.println("boolean type:" + val);
                    }

                }
                // 如果类型是Date
                if (field.getGenericType().toString().equals(
                        "class java.util.Date")) {
                    Method m = (Method) object.getClass().getMethod(
                            "get" + getMethodName(field.getName()));
                    Date val = (Date) m.invoke(object);
                    if (val != null) {
                        System.out.println("Date type:" + val);
                    }

                }
                // 如果类型是Short
                if (field.getGenericType().toString().equals(
                        "class java.lang.Short")) {
                    Method m = (Method) object.getClass().getMethod(
                            "get" + getMethodName(field.getName()));
                    Short val = (Short) m.invoke(object);
                    if (val != null) {
                        System.out.println("Short type:" + val);
                    }

                }
                // 如果还需要其他的类型请自己做扩展

            }//for() --end

        }//if (object!=null )  ----end
    }

    private static String getMethodName(String fildeName) throws Exception {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }
}
