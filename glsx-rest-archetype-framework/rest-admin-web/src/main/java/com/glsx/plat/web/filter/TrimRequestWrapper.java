package com.glsx.plat.web.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class TrimRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> params = new HashMap<>();//保存处理后的参数

    public TrimRequestWrapper(HttpServletRequest request) {
        super(request);
        this.params.putAll(request.getParameterMap());
        this.modifyParameterValues(); //自定义方法，用于参数去重
    }

    private void modifyParameterValues() {
        Set<Map.Entry<String, String[]>> entrys = params.entrySet();
        for (Map.Entry<String, String[]> entry : entrys) {
            String[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim();
            }
            this.params.put(entry.getKey(), values);
        }
    }

    @Override
    public Enumeration<String> getParameterNames() {//重写getParameterNames()
        return new Vector<String>(params.keySet()).elements();
    }


    @Override
    public String getParameter(String name) {//重写getParameter()
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {//重写getParameterValues()
        return params.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() { //重写getParameterMap()
        return this.params;
    }

    /**
     * 重写getInputStream方法  post类型的请求参数必须通过流才能获取到值
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        //非json类型，直接返回
        if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(super.getHeader(HttpHeaders.CONTENT_TYPE))) {
            return super.getInputStream();
        }
        //为空，直接返回
        String json = IOUtils.toString(super.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.isEmpty(json)) {
            return super.getInputStream();
        }
        Object parse = JSON.parse(json);
        if (parse instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) parse;
            trimJsonArray(jsonArray);
        } else if (parse instanceof JSONObject) {
            trimJsonObject((JSONObject) parse);
        } else {
            log.warn("参数不支持去空格:" + parse);
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(JSON.toJSONString(parse).getBytes("utf-8"));
        return new MyServletInputStream(bis);

    }

    private void trimJsonArray(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            trimJsonObject(jsonObject);
        }
    }

    private void trimJsonObject(JSONObject jsonObject) {
        Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            if (value instanceof JSONArray) {
                trimJsonArray((JSONArray) value);
            } else if (value instanceof JSONObject) {
                trimJsonObject((JSONObject) value);
            } else if (value instanceof String) {
                String trimValue = StringUtils.trim(ObjectUtils.toString(value));
                next.setValue(trimValue);
            }
        }
    }

    class MyServletInputStream extends ServletInputStream {
        private ByteArrayInputStream bis;

        public MyServletInputStream(ByteArrayInputStream bis) {
            this.bis = bis;
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }

        @Override
        public int read() throws IOException {
            return bis.read();
        }
    }


}
