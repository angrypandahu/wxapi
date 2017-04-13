package com.domain.api

import com.util.WxUtils
import com.utils.DateUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONObject

@Transactional
class WxService {

    def npToken() {
        def apiAccount = ApiAccount.findByAppId(WxUtils.NP_WX_APP_ID)
        try {
            if (apiAccount) {
                def wxParam = [:]
                def tokenStr = WxUtils.doHttp(WxUtils.NP_GET_TOKEN_URL, wxParam, WxUtils.GET, null)
                def jSONObject = new JSONObject(tokenStr)
                if (jSONObject.has("data")) {
                    def dataJSON = jSONObject.getJSONObject("data")
                    def token = dataJSON.getString("token")
                    if (token != apiAccount.accessToken) {
                        def instance = Calendar.getInstance()
                        instance.add(Calendar.SECOND, 60)
                        apiAccount.setExpiresTime(instance.getTime())
                        apiAccount.setAccessToken(token)
                        apiAccount.merge()
                        apiAccount.save(flush: true)
                    }

                }
            }
        } catch (Exception e) {
            log.error(e.getMessage())
        }


        return apiAccount
    }

    def token(ApiAccount apiAccount) {
        def wxParam = [:]
        wxParam.put("appid", apiAccount.appId);
        wxParam.put("secret", apiAccount.secret);
        wxParam.put("grant_type", "client_credential");
        if (apiAccount.appId == WxUtils.NP_WX_APP_ID) {
            return npToken()
        }
        def accessToken = WxUtils.doAll(WxUtils.WX_GET_TOKEN_URL, wxParam, WxUtils.GET)
        def jSONObject = new JSONObject(accessToken)
        def date = new Date()
        if (jSONObject) {
            if (jSONObject.has("access_token")) {
                apiAccount.setAccessToken(jSONObject.getString("access_token"))
                def instance = Calendar.getInstance()
                instance.setTime(date)
                instance.add(Calendar.SECOND, jSONObject.getInt("expires_in"))
                apiAccount.setExpiresTime(instance.getTime())
                apiAccount.save(flush: true)
                log.info(apiAccount.name + "-> token expiresTime " + DateUtils.dateFormat_4.format(apiAccount.expiresTime))
            }
        }
        return apiAccount
    }

    String userGet(ApiAccount apiAccount, String nextOpenId) {
        def wxParam = [:]
        wxParam.put("access_token", apiAccount.accessToken);
        wxParam.put("next_openid", nextOpenId ? nextOpenId : "");
        return WxUtils.doAll(WxUtils.WX_USER_GET_URL, wxParam, WxUtils.GET)
    }


}
