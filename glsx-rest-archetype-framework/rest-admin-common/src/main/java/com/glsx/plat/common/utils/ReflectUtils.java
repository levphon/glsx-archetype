package com.glsx.plat.common.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

}
