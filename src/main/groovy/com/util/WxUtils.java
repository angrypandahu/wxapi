package com.util;

import com.domain.wechat.WxUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.grails.web.json.JSONObject;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class WxUtils {

    final public static String NP_WX_APP_ID = "wx34d51256308e1e83";
    final public static String WX_GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    final public static String NP_GET_TOKEN_URL = "http://applbsx.nipponpaint.com.cn/v1/get_wx_token";
    final public static String WX_USER_GET_URL = "https://api.weixin.qq.com/cgi-bin/user/get";
    final public static String WX_USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info";
    final public static String WX_GROUP_GET_URL = "https://api.weixin.qq.com/cgi-bin/groups/get";
    final public static String WX_GROUP_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/groups/create";
    final public static String WX_GROUP_UPDATE_URL = "https://api.weixin.qq.com/cgi-bin/groups/update";
    final public static String WX_MEMBERS_UPDATE_URL = "https://api.weixin.qq.com/cgi-bin/groups/members/update";
    final public static String WX_MEMBERS_BATCH_UPDATE_URL = "https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate";
    final public static String WX_CUSTOM_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send";
    final public static String GET = "GET";
    final public static String POST = "POST";

    private static final Log log = LogFactory.getLog(WxUtils.class);


    public static String userInfo(WxUser wxUser) {
        Map<String, String> wxParam = new HashMap<>();
        wxParam.put("access_token", wxUser.getApiAccount().getApiToken().getAccessToken());
        wxParam.put("openid", wxUser.getOpenid());
        String doAll = null;
        try {
            doAll = WxUtils.doAll(WxUtils.WX_USER_INFO_URL, wxParam, WxUtils.GET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doAll;
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static String getUrlStr(String url, Map<String, String> params) {
        String paramStr = getParamStr(params);
        if (!url.equals("") && !paramStr.equals("")) {
            if (url.contains("?")) {
                if (url.substring(url.lastIndexOf("?")).equals("")) {
                    url += paramStr;
                } else {
                    url += "&" + paramStr;
                }
            } else {
                url += "?" + paramStr;
            }
        }
        return url;
    }

    private static String getParamStr(Map<String, String> params) {
        StringBuilder paramStr = new StringBuilder();
        if (params != null && params.size() > 0) {
            Set<String> keys = params.keySet();
            for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                paramStr.append(key);
                paramStr.append("=");
                paramStr.append(params.get(key));
                if (iterator.hasNext()) {
                    paramStr.append("&");
                }
            }
        }
        return paramStr.toString();
    }

    public static String doAll(String url, Map<String, String> params, String method) throws Exception {
        return doAll(url, params, method, null);


    }

    public static String doAll(String url, Map<String, String> params, String method, JSONObject postData) throws Exception {

        String result = "";
        JSONObject errorJson = new JSONObject();
        BufferedReader in = null;
        try {
//            System.setProperty("javax.net.ssl.keyStore", SERVER_KEY_STORE);
//            System.setProperty("javax.net.ssl.keyStorePassword", SERVER_KEY_STORE_PASSWORD);
//            System.setProperty("https.protocols", "SSLv3");
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
            String urlStr = getUrlStr(url, params);
            log.info(method + ":" + urlStr);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                    new java.security.SecureRandom());
            URL realUrl = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
            connection.setRequestMethod(method);
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
            connection.setDoOutput(true);

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");


            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setReadTimeout(60 * 1000);
            connection.connect();

            if (postData != null && method.equals(POST)) {
                DataOutputStream out = new DataOutputStream(
                        connection.getOutputStream());
                out.write(postData.toString().getBytes("UTF-8"));
                out.flush();
                out.close();
            }

            int responseCode = connection.getResponseCode();
//            System.out.println("ResponseCode:" + responseCode);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
//            if (responseCode != 200) {
//                errorJson.put("errorCode",responseCode);
//                return errorJson.toString();
//
//            } else {
//
//            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        log.info("RESULT:" + result);
        return result;

    }


    public static String doHttp(String url, Map<String, String> params, String method, JSONObject postData) throws Exception {

        String result = "";
        JSONObject errorJson = new JSONObject();
        BufferedReader in = null;
        try {
//            System.setProperty("javax.net.ssl.keyStore", SERVER_KEY_STORE);
//            System.setProperty("javax.net.ssl.keyStorePassword", SERVER_KEY_STORE_PASSWORD);
//            System.setProperty("https.protocols", "SSLv3");
//            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
            String urlStr = getUrlStr(url, params);
            log.info(method + ":" + urlStr);
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
//                    new java.security.SecureRandom());
            URL realUrl = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod(method);
//            connection.setSSLSocketFactory(sc.getSocketFactory());
//            connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
            connection.setDoOutput(true);

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");


            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setReadTimeout(60 * 1000);
            connection.connect();

            if (postData != null && method.equals(POST)) {
                DataOutputStream out = new DataOutputStream(
                        connection.getOutputStream());

                out.writeBytes(postData.toString());
                out.flush();
                out.close();
            }

            int responseCode = connection.getResponseCode();
//            System.out.println("ResponseCode:" + responseCode);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
//            if (responseCode != 200) {
//                errorJson.put("errorCode",responseCode);
//                return errorJson.toString();
//
//            } else {
//
//            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        log.debug("RESULT:" + result);
        return result;

    }
}
