package com.glsx.plat.common.utils;

import com.google.gson.*;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author payu
 */
public class JsonSortUtil {

    /**
     * 定义比较规则
     *
     * @return
     */
    private static Comparator<String> getComparator() {
        return String::compareTo;
    }

    /**
     * 排序
     *
     * @param e
     */
    public static void sort(JsonElement e) {
        if (e.isJsonNull() || e.isJsonPrimitive()) {
            return;
        }

        if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            Iterator<JsonElement> it = a.iterator();
            it.forEachRemaining(JsonSortUtil::sort);
            return;
        }

        if (e.isJsonObject()) {
            Map<String, JsonElement> tm = new TreeMap<>(getComparator());
            for (Map.Entry<String, JsonElement> en : e.getAsJsonObject().entrySet()) {
                tm.put(en.getKey(), en.getValue());
            }

            String key;
            JsonElement val;
            for (Map.Entry<String, JsonElement> en : tm.entrySet()) {
                key = en.getKey();
                val = en.getValue();
                e.getAsJsonObject().remove(key);
                e.getAsJsonObject().add(key, val);
                sort(val);
            }
        }
    }

    /**
     * 根据json key排序
     *
     * @param json
     * @return
     */
    public static String sortJson(String json) {
        Gson g = new GsonBuilder().create();
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(json);
        sort(e);
        return g.toJson(e);
    }

    public static void main(String[] args) {
        String json = "{\n" +
                "  \"sign\": \"96484861ECCA61B280971FE369649D7F\",\n" +
                "  \"orders\": [\n" +
                "    {\n" +
                "      \"shopProperty\": \"集团内4S店\",\n" +
                "      \"customerName\": \"洪意琴\",\n" +
                "      \"financialProducts\": \"1\",\n" +
                "      \"periods\": \"012\",\n" +
                "      \"packages\": \"基础套餐\",\n" +
                "      \"settleAmount\": 1690.00,\n" +
                "      \"orderNo\": \"1000012525\",\n" +
                "      \"status\": \"1\",\n" +
                "      \"baseProfit\": 480.00,\n" +
                "      \"shopName\": \"广西弘骏汽车销售服务有限公司\",\n" +
                "      \"crAmount\": 3180.00\n" +
                "    }\n" +
                "  ],\n" +
                "  \"appKey\": \"HTSA\"\n" +
                "}";
        System.out.println(sortJson(json));
    }

}
