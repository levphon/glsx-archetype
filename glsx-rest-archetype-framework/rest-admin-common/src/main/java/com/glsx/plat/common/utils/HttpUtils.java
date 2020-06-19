package com.glsx.plat.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * http请求
 *
 * @
 */
@Slf4j
public class HttpUtils {

    int i = 0;

    /**
     * post请求 ，超时默认10秒, 默认utf-8
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public String post(String url, Map<String, String> params) throws Exception {
        return this.post(url, params, 10, HTTP.UTF_8);
    }

    /**
     * post请求, 超时默认10秒
     *
     * @param url
     * @param params
     * @param charset 编码方式
     * @return
     * @throws Exception
     */
    public String post(String url, Map<String, String> params, String charset) throws Exception {
        return this.post(url, params, 10, charset);
    }

    public String postSms(String url, Map<String, String> params, String charset) throws Exception {
        return this.postsms(url, params, 10, charset);
    }

    /**
     * post请求, 默认utf-8
     *
     * @param url
     * @param params
     * @param timeout 超时时间，秒
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public String post(String url, Map<String, String> params, int timeout) throws Exception {
        return this.post(url, params, timeout, HTTP.UTF_8);
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @param timeout 超时时间，秒
     * @return
     * @throws IOException
     */
    public String post(String url, Map<String, String> params, int timeout, String charset) throws Exception {
//    	if(log.isInfoEnabled()){
//    		log.info("connection url = " +url);
//    		log.info("params = "+params.toString());
//    	}
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter("http.socket.timeout", timeout * 1000);
        httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
        String retVal = "";
        try {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    formparams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, charset);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(entity);
            HttpResponse resp = httpclient.execute(httppost);
            retVal = EntityUtils.toString(resp.getEntity(), charset);
        } catch (IOException e) {
            log.error(" post error : ", e);
            throw e;
        } finally {
            httpclient.getConnectionManager().shutdown();
            if (log.isInfoEnabled()) {
//        		log.info("connection  end,sartTime = "+startTime+"  , endTime =" + endTime +" , times:"+(endTime-startTime));
            }
        }
        return retVal;
    }

    /**
     * post请求
     *
     * @param url
     * @param json
     * @return
     * @throws ParseException
     * @throws IOException
     */
    @SuppressWarnings({"deprecation", "resource"})
    public String postJson(String url, String json) throws Exception {
        String retVal = "";
        DefaultHttpClient httpClient = null;
        if (url.contains("https")) {
            httpClient = new SSLClient();
        } else {
            httpClient = new DefaultHttpClient();
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");

        StringEntity se = new StringEntity(json.toString(), "UTF-8");
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpPost.setEntity(se);

        HttpResponse resp = httpClient.execute(httpPost);
        retVal = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
        return retVal;
    }

    /**
     * 发送post请求到Google短链接服务器，获取url的短链接
     *
     * @param url
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public String postGoogleURLShortener(String url) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter("http.socket.timeout", 10000);
        httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
        String retVal = url;
        try {
            HttpPost httppost = new HttpPost("https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyABoOMQswr72iix40kj1I42Dk7tBf0kppk");
            httppost.setHeader("Content-Type", "application/json");
            httppost.setEntity(new StringEntity("{\"longUrl\": \"" + url + "\"}"));
            HttpResponse resp = httpclient.execute(httppost);
            String jsonStr = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
            if (StringUtils.isNotEmpty(jsonStr)) {
                JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                if (jsonObj.containsKey("id")) {
                    retVal = jsonObj.getString("id");
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return retVal;
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param timeout 超时时间，秒
     * @param charset 编码方式
     * @return
     * @throws Exception
     */
    public String get(String url, Map<String, String> params, int timeout, String charset) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter("http.socket.timeout", timeout * 1000);
        httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
        String retVal = "";
        try {
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    qparams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }
            String paramstr = URLEncodedUtils.format(qparams, charset);
            if (StringUtils.isNotEmpty(paramstr)) {
                url = url + "?" + paramstr;
            }
            HttpGet httpget = new HttpGet(url);

            HttpResponse resp = httpclient.execute(httpget);
            retVal = EntityUtils.toString(resp.getEntity(), charset);
        } catch (IOException e) {
            throw e;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return retVal;
    }


    /**
     * get请求
     *
     * @param url
     * @param params
     * @param timeout      超时时间，秒
     * @param charset      编码方式
     * @param fNameEndChar 方法名后结束字符 默认“?”
     * @return
     * @throws Exception
     */
    public static String get(String url, Map<String, String> params, int timeout, String charset, String fNameEndChar) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter("http.socket.timeout", timeout * 1000);
        httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
        String retVal = "";
        fNameEndChar = (fNameEndChar == null || "".endsWith(fNameEndChar)) ? "?" : fNameEndChar;
        try {
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    qparams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }
            String paramstr = URLEncodedUtils.format(qparams, charset);
            if (StringUtils.isNotEmpty(paramstr)) {
                url = url + fNameEndChar + paramstr;
            }
            HttpGet httpget = new HttpGet(url);

            HttpResponse resp = httpclient.execute(httpget);
            retVal = EntityUtils.toString(resp.getEntity(), charset);
        } catch (IOException e) {
            throw e;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return retVal;
    }

    /**
     * get请求,超时默认10秒
     *
     * @param url
     * @param params
     * @param charset 编码方式
     * @return
     * @throws IOException
     */
    public String get(String url, Map<String, String> params, String charset) throws Exception {
        return this.get(url, params, 10, charset);
    }

    /**
     * get请求,超时默认10秒, 默认utf-8
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public String get(String url, Map<String, String> params) throws Exception {
        return this.get(url, params, 10, HTTP.UTF_8);
    }

    /**
     * get请求, 默认utf-8
     *
     * @param url
     * @param params
     * @param timeout 超时时间，秒
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public String get(String url, Map<String, String> params, int timeout) throws Exception {
        return this.get(url, params, timeout, HTTP.UTF_8);
    }

    public String postXml(String url, byte[] b, int timeout, String charset) {
        String retVal = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setIntParameter("http.socket.timeout", timeout * 1000);
            httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
            HttpPost httppost = new HttpPost(url);
            ByteArrayEntity entity = new ByteArrayEntity(b);
            httppost.setEntity(entity);
            HttpResponse resp = httpclient.execute(httppost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpentity = resp.getEntity();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpentity.getContent(), "GBK"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    retVal += line;
                }
                bufferedReader.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @SuppressWarnings("deprecation")
    public String postsms(String url, Map<String, String> params, int timeout, String charset) throws IOException {
        long startTime = System.currentTimeMillis();
//    	if(log.isInfoEnabled()){
//    		log.info("connection url = " +url);
//    		log.info("params = "+params.toString());
//    	}

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter("http.socket.timeout", timeout * 1000);
        httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
//        httpclient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
        String retVal = "";
        try {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    formparams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, charset);
            HttpPost httppost = new HttpPost(url);
            httppost.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(10, false));
            httppost.setEntity(entity);
            HttpResponse resp = httpclient.execute(httppost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpentity = resp.getEntity();
                String httpcharset = EntityUtils.getContentCharSet(entity);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpentity.getContent(), httpcharset));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    retVal += line;
                }
                bufferedReader.close();
            }
//            retVal = EntityUtils.toString(resp.getEntity(), charset);
        } catch (IOException e) {
            i++;
            if (i <= 3) {
                postsms(url, params, timeout, charset);
            }
            log.error(" post error : " + " url:" + url + " params" + params, e);
            throw e;
        } finally {
            httpclient.getConnectionManager().shutdown();
            long endTime = System.currentTimeMillis();
            if (log.isInfoEnabled()) {
                log.info("connection  end,sartTime = " + startTime + "  , endTime =" + endTime + " , times:" + (endTime - startTime));
            }
        }
        log.info(" url:" + url + " params" + params);
        log.info("post back" + retVal);
        return retVal;
    }


}