package com.ly.localize.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nali.common.GlobalConstant;

public class HttpClientUtils {

    private final static Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    protected static MultiThreadedHttpConnectionManager httpConnectManager;

    protected static HttpClient httpClient;

    static {
        try {
            httpConnectManager = new MultiThreadedHttpConnectionManager();
            httpClient = new HttpClient(httpConnectManager);
            // 每个host的最大连接数
            httpClient.getHttpConnectionManager().getParams()
                    .setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, 1024);
            // 总连接数
            httpClient.getHttpConnectionManager().getParams().setMaxTotalConnections(1024);
            // 读超时
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(60 * 1000);
            // 连接超时时间
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10 * 60 * 1000);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String post(String url, NameValuePair[] parameters) throws Exception {
        PostMethod method = new PostMethod(url);
        method.addParameters(parameters);
        HttpMethodParams param = method.getParams();
        param.setContentCharset("UTF-8");
        method.setRequestHeader("refer","http://www.qq.com/");
        method.getParams()
                .setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        ByteArrayOutputStream baos = null;
        try {
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != 200) {
                log.warn("execute http post request error, url:{}, parameters:{}, statusCode:{}", url,
                        Arrays.toString(parameters), statusCode);
                return null;
            }
            InputStream is = method.getResponseBodyAsStream();
            baos = new ByteArrayOutputStream();
            int count = 0;
            byte[] c = new byte[2408];
            while ((count = is.read(c)) != -1) {
                baos.write(c, 0, count);
            }
            String result = new String(baos.toByteArray(), GlobalConstant.UTF8);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            method.releaseConnection();
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 测试用
     * 不能用于生产环境，多线程设置 state会有并发问题
     */
    public static String postWithCookies(String url, NameValuePair[] parameters, Cookie[] cookies) throws Exception {
        httpClient.getState().addCookies(cookies);
        return post(url, parameters);
    }

    /**
     * 测试用
     * 不能用于生产环境，多线程设置 state会有并发问题
     */
    public static String getWithCookies(String url, String parameter, Cookie[] cookies) throws Exception {
        httpClient.getState().addCookies(cookies);
        return get(url, parameter);
    }

    public static String get(String url, String parameter) throws Exception {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        if (!StringUtils.isEmpty(parameter)) {
            url = url + "?" + parameter;
        }
        GetMethod method = new GetMethod(url);
        method.getParams()
                .setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        ByteArrayOutputStream baos = null;
        try {
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != 200) {
                log.error("execute http get request error, url:{}, parameters:{}, statusCode:{}", url, parameter,
                        statusCode);
                return null;
            }
            InputStream is = method.getResponseBodyAsStream();
            baos = new ByteArrayOutputStream();
            int count = 0;
            byte[] c = new byte[2408];
            while ((count = is.read(c)) != -1) {
                baos.write(c, 0, count);
            }
            String result = new String(baos.toByteArray(), GlobalConstant.UTF8);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            method.releaseConnection();
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static String assembleGetParams(Map<String, String> params, boolean needEncode) {
        if (null == params || params.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entity : params.entrySet()) {
            try {
                String value = entity.getValue();
                if (needEncode) {
                    if (StringUtils.isEmpty(value)) {
                        value = "";
                    } else {
                        value = URLEncoder.encode(entity.getValue(), "utf-8");
                    }
                }
                sb.append(entity.getKey()).append("=").append(value).append("&");
            } catch (UnsupportedEncodingException e) {
                // would never happen
            }
        }
        return sb.toString();
    }

    public static byte[] getBytesByUrl(String image) throws IOException {
        BufferedInputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(image);
            URLConnection c = url.openConnection();
            c.setConnectTimeout(5000);
            c.setReadTimeout(10000);
            in = new BufferedInputStream(c.getInputStream());
            baos = new ByteArrayOutputStream();
            byte[] picBytes = new byte[1024];
            int len;
            while ((len = in.read(picBytes)) > 0) {
                baos.write(picBytes, 0, len);
            }
            return baos.toByteArray();
        } finally {
            if (null != in) {
                in.close();
            }
            if (null != baos) {
                baos.close();
            }
        }
    }

    public static void downloadFile(String url, String localFile) throws IOException {
        GetMethod method = new GetMethod(url);
        method.addRequestHeader("referer","www.baidu.cn");
        int code = httpClient.executeMethod(method);
        if (code != 200) {
            throw new IOException("can not get url:" + url + ",code:" + code);
        }
        FileOutputStream os = null;
        try {
            InputStream is = method.getResponseBodyAsStream();
            File file = new File(localFile);
            os = new FileOutputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int eof = 0;
            while ((eof = is.read(buffer)) != -1) {
                os.write(buffer, 0, eof);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            method.releaseConnection();
            if (os != null) {
                os.close();
            }
        }
    }
}
