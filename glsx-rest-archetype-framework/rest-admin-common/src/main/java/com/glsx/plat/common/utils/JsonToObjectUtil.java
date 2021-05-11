package com.glsx.plat.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.glsx.plat.common.model.PageDto;
import com.glsx.plat.common.model.ResponseDto;

import java.util.List;

/**
 * 类型装换
 *
 * @author zhouhaibao
 * @date 2021/5/10 10:57
 */
public class JsonToObjectUtil {

    /**
     * R转object类型
     *
     * @param json json字符串
     * @param clazz java类型
     * @param <T> 泛型
     * @return ResponseDto<T>
     */
    public static <T> ResponseDto<T> getObject(String json, Class<T> clazz) {
        ResponseDto responseDto = JSONObject.parseObject(json, ResponseDto.class);
        Integer code = responseDto.getCode();
        if(code==200){
            String data = responseDto.getData();
            T t = JSONObject.parseObject(data, clazz);
            responseDto.setT(t);
        }
        return responseDto;
    }

    /**
     * R转list类型
     *
     * @param json json字符串
     * @param clazz java类型
     * @param <T> 泛型
     * @return ResponseDto<T>
     */
    public static <T> ResponseDto<T> getObjectList(String json, Class<T> clazz) {
        ResponseDto responseDto = JSONObject.parseObject(json, ResponseDto.class);
        Integer code = responseDto.getCode();
        if(code==200){
            String data = responseDto.getData();
            List<T> ts = JSONObject.parseArray(data, clazz);
            responseDto.setTList(ts);
        }
        return responseDto;
    }

    /**
     * R转分页类型
     *
     * @param json json字符串
     * @param clazz java类型
     * @param <T> 泛型
     * @return ResponseDto<T>
     */
    public static <T> ResponseDto<T> getPageList(String json, Class<T> clazz) {
        ResponseDto responseDto = JSONObject.parseObject(json, ResponseDto.class);
        Integer code = responseDto.getCode();
        if(code==200){
            String data = responseDto.getData();
            PageDto pageDto = JSONObject.parseObject(data, PageDto.class);
            responseDto.setPageDto(pageDto);
        }
        return responseDto;
    }

}
