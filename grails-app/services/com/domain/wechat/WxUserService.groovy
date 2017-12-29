package com.domain.wechat

import com.util.WxUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONObject

@Transactional
class WxUserService {


    def mergeSave(List<WxUser> wxUserList, List<WxUser> dbList) {
        List<WxUser> notInDb = new ArrayList<>()
        def openIdDbList = dbList?.openid
        wxUserList?.openid?.eachWithIndex { String openid, int i ->
            if (!openIdDbList.contains(openid)) {
                notInDb.add(wxUserList.get(i))
            }
        }
        log.debug("notInDb.size=" + notInDb.size())
        WxUser.withNewTransaction {
            notInDb?.each {
                it.save(flush: true)
            }
        }
    }

    def batchSave(List<WxUser> wxUserList) {
        def begin = new Date()
        log.debug("#########batchSave##begin->" + begin)
        def whereQuery = WxUser.where {
            (openid in wxUserList.openid) && (apiAccount == wxUserList.get(0).apiAccount)
        }
        def existsUsers = whereQuery.findAll()
        mergeSave(wxUserList, existsUsers)
        log.debug(existsUsers.id.toString())
        def end = new Date()
        def time = (end.getTime() - begin.getTime()) / 1000
        log.debug("#########batchSave##end->" + end)
        log.debug("#########batchSave[total:" + wxUserList.size() + ",time:" + time + "]")
    }

    def batchUpdate(List<WxUser> wxUserList, boolean withNewTransaction) {
        def begin = new Date()
        log.debug("#########batchUpdate##begin->" + begin)
        if (withNewTransaction) {
            WxUser.withNewTransaction {
                wxUserList?.each {
                    it.save(flush: true)
                }
            }
        } else {
            wxUserList?.each {
                it.save()
            }
        }


        def end = new Date()
        def time = (end.getTime() - begin.getTime()) / 1000
        log.debug("#########batchUpdate##end->" + end)
        log.debug("#########batchUpdate[total:" + wxUserList.size() + ",time:" + time + "]")
    }

    def batchUpdate(List<WxUser> wxUserList) {
        batchUpdate(wxUserList, false)
    }

    String userInfo(WxUser wxUser) {
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

    def userInfo(JSONObject jSONObject, WxUser wxUser) {
        if (jSONObject && jSONObject.has("openid")) {
            def openid = jSONObject.getString("openid")
            if (openid == wxUser.openid) {
                if (jSONObject.has("subscribe")) wxUser.setSubscribe(jSONObject.getInt("subscribe"))
                if (jSONObject.has("sex")) wxUser.setSex(jSONObject.getInt("sex"))
                if (jSONObject.has("nickname")) wxUser.setNickname(jSONObject.getString("nickname").getBytes("UTF-8"))
                if (jSONObject.has("language")) wxUser.setLanguage(jSONObject.getString("language"))
                if (jSONObject.has("city")) wxUser.setCity(jSONObject.getString("city").getBytes("UTF-8"))
                if (jSONObject.has("province")) wxUser.setProvince(jSONObject.getString("province").getBytes("UTF-8"))
                if (jSONObject.has("country")) wxUser.setCountry(jSONObject.getString("country"))
                if (jSONObject.has("headimgurl")) wxUser.setHeadimgurl(jSONObject.getString("headimgurl"))
                if (jSONObject.has("subscribe_time")) wxUser.setSubscribetime(jSONObject.getLong("subscribe_time"))
                if (jSONObject.has("unionid")) wxUser.setUnionid(jSONObject.getString("unionid"))
                if (jSONObject.has("remark")) wxUser.setRemark(jSONObject.getString("remark"))
                wxUser.setIsDelete(false);
                if (jSONObject.has("groupid")) {
                    def groupid = jSONObject.getInt("groupid")
                    wxUser.setGroupid(groupid)
                    def wxGroup = WxGroup.findByWxId(groupid)
                    wxUser.setWxGroup(wxGroup)
                }
                def array = jSONObject.getJSONArray("tagid_list")
                def list = []
                if (array.size() > 0) {
                    array.each {
                        list.add(it.toString())
                    }
                    wxUser.setTagidlist(list as String[])
                }

//                wxUser.save(flush: true)
            }
        }
    }

    def customSend(WxUser wxUser, String msg) {
        def apiAccount = wxUser.apiAccount
        def wxParam = [:]
        wxParam.put("access_token", apiAccount.apiToken.accessToken);
        def object = new JSONObject();
        object.put("touser", wxUser.openid)
        object.put("msgtype", "text")
        def text = new JSONObject()
        text.put("content", msg)
        object.put("touser", wxUser.openid)
        object.put("text", text)
        return WxUtils.doAll(WxUtils.WX_CUSTOM_SEND_URL, wxParam, WxUtils.POST, object);

    }


}
