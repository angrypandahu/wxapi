package com.domain.wechat

import com.batch.WxBatchGetUserInfo
import com.domain.api.ApiAccount
import com.util.WxUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONObject

@Transactional
class WxUserService {



    int getWxUserInfo(ApiAccount apiAccount) {
        int limit = 10000;
        Integer total = WxUser.countByApiAccount(apiAccount)
        int page = total / limit;
        if (total % limit > 0) {
            page++;
        }
        for (int i = 0; i < page; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("max", limit);
            map.put("offset", limit * i);
            map.put("sort", "id");
            map.put("apiAccount", apiAccount);
            List<WxUser> wxUserList = WxUser.list(map);
            new WxBatchGetUserInfo(wxUserList, this).batch(wxUserList.size())
        }
        return 0;
    }

    def mergeSave(List<WxUser> wxUserList, List<WxUser> dbList) {
        List<WxUser> notInDb = new ArrayList<>()
        def openIdDbList = dbList?.openid
        wxUserList?.openid?.eachWithIndex { String openid, int i ->
            if (!openIdDbList.contains(openid)) {
                notInDb.add(wxUserList.get(i))
            }
        }
        log.info("notInDb.size=" + notInDb.size())
        WxUser.withNewTransaction {
            notInDb?.each {
                it.save(flush: true)
            }
        }
    }

    def batchSave(List<WxUser> wxUserList) {
        def begin = new Date()
        log.info("#########batchSave##begin->" + begin)
        def whereQuery = WxUser.where {
            (openid in wxUserList.openid) && (apiAccount == wxUserList.get(0).apiAccount)
        }
        def existsUsers = whereQuery.findAll()
        mergeSave(wxUserList, existsUsers)
        log.info(existsUsers.id.toString())
        def end = new Date()
        def time = (end.getTime() - begin.getTime()) / 1000
        log.info("#########batchSave##end->" + end)
        log.info("#########batchSave[total:" + wxUserList.size() + ",time:" + time + "]")
    }

    def batchUpdate(List<WxUser> wxUserList) {
        def begin = new Date()
        log.info("#########batchUpdate##begin->" + begin)
        WxUser.withNewTransaction {
            wxUserList?.each {
                it.save(flush: true)
            }
        }
        def end = new Date()
        def time = (end.getTime() - begin.getTime()) / 1000
        log.info("#########batchUpdate##end->" + end)
        log.info("#########batchUpdate[total:" + wxUserList.size() + ",time:" + time + "]")
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
                if (jSONObject.has("groupid")) {
                    def groupid = jSONObject.getInt("groupid")
                    wxUser.setGroupid(groupid)
                    def wxGroup = WxGroup.findByWxId(groupid)
                    wxUser.setWxGroup(wxGroup)
                }
                def array = jSONObject.getJSONArray("tagid_list")
                if (array?.size() > 0) {
                    println "tagid_list:" + array
                }
//                wxUser.save(flush: true)
            }
        }
    }

    def customSend(WxUser wxUser, String msg) {
        def apiAccount = wxUser.apiAccount
        def wxParam = [:]
        wxParam.put("access_token", apiAccount.accessToken);
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
