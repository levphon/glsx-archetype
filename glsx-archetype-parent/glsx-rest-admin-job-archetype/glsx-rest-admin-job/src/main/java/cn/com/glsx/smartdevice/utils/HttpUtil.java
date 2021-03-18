package cn.com.glsx.smartdevice.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class HttpUtil {

    private static final Log logger = LogFactory.getLog(HttpUtil.class);

    final static int CONNECTION_TIMEOUT = 10000; // 设置请求超时 10 秒钟 根据业务调整
    final static int SOCKET_TIMEOUT = 180000; // 数据传输时间 3 分钟
    final static int SEARCH_CONNECTION_TIMEOUT = 500; // 连接不够用的时候等待超时时间,不设置默认等于 CONNECTION_TIMEOUT

    public final static ContentType JSON_UTF8 = ContentType.create("application/json", "utf-8");

    static PoolingHttpClientConnectionManager httpClientConnectionManager = null;

    static {
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory()).build();

        httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        httpClientConnectionManager.setMaxTotal(200); // 总的连接池的大小
        httpClientConnectionManager.setDefaultMaxPerRoute(20); // 对每个主机的最大连接大小,每个主机最大20 个连接,总共 200 个连接,支持 10 台主机
    }

    public static CloseableHttpClient getHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(SEARCH_CONNECTION_TIMEOUT).build();
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(true)
                .setTcpNoDelay(true).build();
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 3) {// 如果已经重试了 3 次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(httpRequestRetryHandler)
                .setDefaultSocketConfig(socketConfig).build();
        return httpClient;
    }

    /**
     * 作者:sanri <br/>
     * 时间:2017-7-26下午3:47:02<br/>
     * 功能:将 map 型 的参数转换为NameValuePair 类型  <br/>
     *
     * @param params 注:日期将会转格式为  yyyy-MM-dd
     * @return
     */
    public static List<NameValuePair> transferParam(Map<String, ? extends Object> params) {
        return transferParam(params, "yyyy-MM-dd");
    }

    public static List<NameValuePair> transferParam(Map<String, ? extends Object> params, String dateFormat) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (params != null && !params.isEmpty()) {
            Set<?> entrySet = params.entrySet();
            Iterator<?> paramIterator = entrySet.iterator();
            while (paramIterator.hasNext()) {
                Map.Entry<String, ? extends Object> param = (Map.Entry<String, ? extends Object>) paramIterator.next();
                Object value = param.getValue();
                if (value == null) {
                    nameValuePairs.add(new BasicNameValuePair(param.getKey(), null));
                } else {
                    if (value instanceof Date) {
                        Date date = (Date) value;
                        String dateString = "";
                        if (StringUtils.isBlank(dateFormat)) {
                            dateString = date.toString();
                        } else {
                            dateString = DateFormatUtils.format(date, dateFormat);
                        }
                        nameValuePairs.add(new BasicNameValuePair(param.getKey(), dateString));
                        continue;
                    }
                    nameValuePairs.add(new BasicNameValuePair(param.getKey(), ObjectUtils.toString(value)));
                }
            }
        }
        return nameValuePairs;
    }

    /**
     * http发送post请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static JSONObject sendPost(String url, String json, Map<String, String> headerParams) throws IOException {
        String formData = postData(url, json, headerParams);
        if (StringUtils.isNotBlank(formData)) {
            return JSONObject.parseObject(formData);
        }
        return new JSONObject();
    }

    /**
     * 作者:sanri <br/>
     * 时间:2017-10-19下午2:25:40<br/>
     * 功能:向指定 url 提交数据 <br/>
     *
     * @param url
     * @param data
     * @return
     * @throws IOException
     */
    public static String postData(String url, String data, Map<String, String> headerParams) throws IOException, HTTPException {
        HttpClient httpClient = getHttpClient();
        //请求头,请求体数据封装
        HttpPost postMethod = new HttpPost(url);
        postMethod.addHeader("Content-Type", "application/json");
        if (!headerParams.isEmpty()) {
            headerParams.forEach((key, value) -> {
                postMethod.addHeader(key, value);
            });
        }
        postMethod.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
        StringEntity dataEntity = new StringEntity(data, JSON_UTF8);
        postMethod.setEntity(dataEntity);
        HttpResponse response = null;
        try {
            //开始请求,记录请求数据和请求时间
            response = httpClient.execute(postMethod);
            //获取响应
            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode != 200) {
                HTTPException exception = new HTTPException(statusCode);
                logger.error("请求地址" + url + "出错,http 状态码为:" + statusCode, exception);
                throw exception;
            }
            String retData = EntityUtils.toString(entity, JSON_UTF8.getCharset());
            return retData;
        } catch (ClientProtocolException e) {
            throw new IllegalArgumentException("客户端协议错误 ，检查 url 配置 url: " + url, e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }


    /**
     * 作者:sanri <br/>
     * 时间:2017-10-19下午2:25:40<br/>
     * 功能:向指定 url 提交数据 <br/>
     *
     * @param url
     * @param data
     * @return
     * @throws IOException
     */
    public static String putData(String url, String data, Map<String, String> headerParams) throws IOException, HTTPException {
        HttpClient httpClient = getHttpClient();
        //请求头,请求体数据封装
        HttpPut postMethod = new HttpPut(url);
        postMethod.addHeader("Content-Type", "application/json");
        if (!headerParams.isEmpty()) {
            headerParams.forEach((key, value) -> {
                postMethod.addHeader(key, value);
            });
        }
        postMethod.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
        StringEntity dataEntity = new StringEntity(data, JSON_UTF8);
        postMethod.setEntity(dataEntity);
        HttpResponse response = null;
        try {
            //开始请求,记录请求数据和请求时间
            response = httpClient.execute(postMethod);
            //获取响应
            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode != 200) {
                HTTPException exception = new HTTPException(statusCode);
                logger.error("请求地址" + url + "出错,http 状态码为:" + statusCode, exception);
                throw exception;
            }
            String retData = EntityUtils.toString(entity, JSON_UTF8.getCharset());
            return retData;
        } catch (ClientProtocolException e) {
            throw new IllegalArgumentException("客户端协议错误 ，检查 url 配置 url: " + url, e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }


    /**
     * 作者:sanri <br/>
     * 时间:2017-10-19下午2:25:40<br/>
     * 功能:向指定 url 提交数据 <br/>
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String deleteData(String url, Map<String, String> headerParams) throws IOException, HTTPException {
        HttpClient httpClient = getHttpClient();
        //请求头,请求体数据封装
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader("Content-Type", "application/json");
        if (!headerParams.isEmpty()) {
            headerParams.forEach((key, value) -> {
                httpDelete.addHeader(key, value);
            });
        }
        httpDelete.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
        HttpResponse response = null;
        try {
            //开始请求,记录请求数据和请求时间
            response = httpClient.execute(httpDelete);
            //获取响应
            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode != 200) {
                HTTPException exception = new HTTPException(statusCode);
                logger.error("请求地址" + url + "出错,http 状态码为:" + statusCode, exception);
                throw exception;
            }
            String retData = EntityUtils.toString(entity, JSON_UTF8.getCharset());
            return retData;
        } catch (ClientProtocolException e) {
            throw new IllegalArgumentException("客户端协议错误 ，检查 url 配置 url: " + url, e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }

    /**
     * 作者:sanri <br/>
     * 时间:2018-3-28下午8:57:50<br/>
     * 功能: 请求地址获取数据<br/>
     *
     * @param url
     * @param urlEncoded
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static String getData(final String url, final String urlEncoded, Map<String, String> headerParams) throws IOException {
        HttpClient httpClient = getHttpClient();
        String queryUrl = url;
        if (StringUtils.isNotBlank(urlEncoded)) {
            queryUrl += "?" + urlEncoded;
        }
        HttpGet httpGet = new HttpGet(queryUrl);
        if (!headerParams.isEmpty()) {
            headerParams.forEach((key, value) -> {
                httpGet.addHeader(key, value);
            });
        }
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode != 200) {
                HTTPException exception = new HTTPException(statusCode);
                logger.error("请求地址" + url + "出错,http 状态码为:" + statusCode, exception);
                throw exception;
            }
            HttpEntity msgEntity = response.getEntity();
            String message = EntityUtils.toString(msgEntity);
            return message;
        } catch (ClientProtocolException e) {
            throw new IllegalArgumentException("客户端协议错误 ，检查 url 配置 url: " + url, e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }

    /**
     * 作者:sanri <br/>
     * 时间:2017-7-26下午3:36:40<br/>
     * 功能:兼容以前的功能，传入路径和参数，返回字符串的返回结果 <br/>
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String getData(String url, Map<String, String> params, Charset charset, Map<String, String> headerParams) throws IOException {
        List<NameValuePair> nameValuePairs = transferParam(params);
        HttpEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, charset);
        String urlEncoded = EntityUtils.toString(urlEncodedFormEntity, charset);
        String data = getData(url, urlEncoded, headerParams);
        return data;
    }

    /**
     * http发送get请求
     *
     * @param url
     * @param params
     * @param headerParams
     * @return
     * @throws IOException
     */
    public static JSONObject sendGet(String url, Map<String, String> params, Map<String, String> headerParams) throws IOException {
        String retData = getData(url, params, Consts.UTF_8, headerParams);
        if (StringUtils.isNotBlank(retData)) {
            return JSONObject.parseObject(retData);
        }
        return new JSONObject();
    }

    /**
     * http的put方法
     *
     * @param url 请求url
     * @param json 请求body
     * @param headerParams 请求参数
     * @return
     * @throws IOException
     */
    public static JSONObject sendPut(String url, String json, Map<String, String> headerParams) throws IOException {
        String formData = putData(url, json, headerParams);
        if (StringUtils.isNotBlank(formData)) {
            return JSONObject.parseObject(formData);
        }
        return new JSONObject();
    }

    /**
     * http发送delete请求
     *
     * @param url url
     * @param headerParams 请求参数
     * @return
     * @throws IOException
     */
    public static JSONObject sendDelete(String url,Map<String, String> headerParams) throws IOException {
        String retData = deleteData(url,headerParams);
        if (StringUtils.isNotBlank(retData)) {
            return JSONObject.parseObject(retData);
        }
        return new JSONObject();
    }

}