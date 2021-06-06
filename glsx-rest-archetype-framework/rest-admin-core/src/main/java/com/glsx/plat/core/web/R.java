package com.glsx.plat.core.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 */
public class R<T> extends HashMap<String, Object> {

    public final static String CODE_KEY = "code";
    public final static String MSG_KEY = "message";

    private final static String DATA_KEY = "data";
    private final static String LIST_KEY = "list";
    private final static String ITEMS_KEY = "items";
    private final static String PAGE_KEY = "page";
    private final static String TOTAL_PAGES_KEY = "totalPages";
    private final static String TOTAL_ELEMENTS_KEY = "totalElements";
    private final static int SUCCESS_CODE = 200;

    public R() {
        put(CODE_KEY, SUCCESS_CODE);
        put(MSG_KEY, "success");
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put(CODE_KEY, code);
        r.put(MSG_KEY, msg);
        return r;
    }

    public static R error(String code, String msg) {
        R r = new R();
        r.put(CODE_KEY, code);
        r.put(MSG_KEY, msg);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public static R ok(String msg) {
        R r = new R();
        r.put(MSG_KEY, msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public R data(T o) {
        super.put(DATA_KEY, o);
        return this;
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 分页信息，需要前端自己算分页数
     *
     * @param value
     * @param totalElements
     * @return
     */
    public R putPageData(T value, long totalElements) {
        Map data = new HashMap(2);
        data.put(LIST_KEY, value);
        data.put(TOTAL_ELEMENTS_KEY, totalElements);
        super.put(DATA_KEY, data);
        return this;
    }

    /**
     * spring jdbc分页信息
     *
     * @param page
     * @return
     */
    public R putPageData(org.springframework.data.domain.Page page) {
        Map data = new HashMap(3);
        data.put(LIST_KEY, page.getContent());//分页数据
        data.put(TOTAL_PAGES_KEY, page.getTotalPages());//总页数
        data.put(TOTAL_ELEMENTS_KEY, page.getTotalElements());//总数
        super.put(DATA_KEY, data);
        return this;
    }

    /**
     * mybatis pagehelper分页信息
     *
     * @param page
     * @return
     */
    public R putPageData(com.github.pagehelper.PageInfo page) {
        Map data = new HashMap(3);
        data.put(LIST_KEY, page.getList());//分页数据
        data.put(TOTAL_PAGES_KEY, page.getPages());//总页数
        data.put(TOTAL_ELEMENTS_KEY, page.getTotal());//总数
        super.put(DATA_KEY, data);
        return this;
    }

    /**
     * mybatis pagehelper分页信息
     *
     * @param page
     * @return
     */
    public R putPageData(com.github.pagehelper.Page<?> page, Collection<?> coll) {
        Map data = new HashMap(2);
        data.put(PAGE_KEY, page);//分页信息
        data.put(ITEMS_KEY, coll);//分页数据
        super.put(DATA_KEY, data);
        return this;
    }

    /**
     * 分页信息，需要前端自己算分页数
     *
     * @param page
     * @return
     */
    public R putPageData(Pagination page) {
        Map data = new HashMap(2);
        data.put(LIST_KEY, page.getList());
        data.put(TOTAL_ELEMENTS_KEY, page.getTotal());//总数
        super.put(DATA_KEY, data);
        return this;
    }

    /**
     * 分页信息，需要前端自己算分页数
     *
     * @param page
     * @return
     */
    public R putPageData(Pagination pagination, cn.hutool.db.Page page) {
        Map data = new HashMap(3);
        data.put(PAGE_KEY, page);//分页信息
        data.put(LIST_KEY, pagination.getList());//数据列表
        data.put(TOTAL_ELEMENTS_KEY, pagination.getTotal());//总数
        super.put(DATA_KEY, data);
        return this;
    }

    public String getMessage() {
        return (String) this.get(MSG_KEY);
    }

    public void setMessage(String formatMessage) {
        this.put(MSG_KEY, formatMessage);
    }

    public int getCode() {
        return (int) this.get(CODE_KEY);
    }

    public void setCode(int code) {
        this.put(CODE_KEY, code);
    }

    public Object getData() {
        return this.get(DATA_KEY);
    }

    public <T> T getData(Class<T> clazz) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(this.get(DATA_KEY));
        return gson.fromJson(json, clazz);
    }

    public <T> T getDataList(Type type) {
        Gson gson = new GsonBuilder().create();
        String arrayJson = gson.toJson(this.get(DATA_KEY));
        T list = gson.fromJson(arrayJson, type);
        return list;
    }

    public Class<T> getGenericType() {
        Type type = this.getClass().getGenericSuperclass();
        Type[] parameter = ((ParameterizedType) type).getActualTypeArguments();
        return (Class<T>) parameter[0];
    }

    public boolean isSuccess() {
        return SUCCESS_CODE == this.getCode();
    }

}
