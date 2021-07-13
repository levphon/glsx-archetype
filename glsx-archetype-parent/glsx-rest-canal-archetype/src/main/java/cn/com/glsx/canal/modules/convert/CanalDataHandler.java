package cn.com.glsx.canal.modules.convert;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouhaibao
 * @version 1.0
 * @description: 数据库字段和实体类之间的转化
 * @date 2021/6/23 14:36
 */
public class CanalDataHandler extends TypeConvertHandler {

    public static <T> T ConvertToBean(List<CanalEntry.Column> columnList, Class<T> clazz) {
        T bean = null;
        try {
            bean = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            Field.setAccessible(fields, true);

            Map<String, Field> fieldMap = new HashMap<>(fields.length);

            for (Field field : fields) {
                fieldMap.put(camelToUnderline(field.getName(), 0), field);
            }

            if (fieldMap.containsKey("serialVersionUID")) {
                fieldMap.remove("serialVersionUID".toLowerCase());
            }

            for (CanalEntry.Column column : columnList) {
                String columnName = column.getName();
                String columnValue = column.getValue();
                if (fieldMap.containsKey(columnName)) {
                    Field field = fieldMap.get(columnName);
                    Class<?> type = field.getType();
                    if (BEAN_FIELD_TYPE.containsKey(type)) {
                        switch (BEAN_FIELD_TYPE.get(type)) {
                            case "Integer":
                                field.set(bean, parseToInteger(columnValue));
                                break;
                            case "Long":
                                field.set(bean, parseToLong(columnValue));
                                break;
                            case "Double":
                                field.set(bean, parseToDouble(columnValue));
                                break;
                            case "String":
                                field.set(bean, columnValue);
                                break;
                            case "java.util.Date":
                                field.set(bean, parseToDate(columnValue));
                                break;
                            case "java.sql.Date":
                                field.set(bean, parseToSqlDate(columnValue));
                                break;
                            case "java.sql.Timestamp":
                                field.set(bean, parseToTimestamp(columnValue));
                                break;
                            case "java.sql.Time":
                                field.set(bean, parseToTime(columnValue));
                                break;
                            case "java.time.LocalDateTime":
                                field.set(bean, parseStringToDateTime(columnValue));
                                break;
                            case "java.math.BigDecimal":
                                field.set(bean, parseStringToBigDecimal(columnValue));
                                break;
                            default:
                                break;
                        }
                    } else {
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("无法转换对象");
        }
        return bean;
    }

    //驼峰转下划线
    public static String camelToUnderline(String param, Integer charType) {
        char UNDERLINE = '_';
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
            }
            if (charType == 2) {
                //统一都转大写
                sb.append(Character.toUpperCase(c));
            } else {
                //统一都转小写
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();

    }
}