package com.domain.wechat

import com.domain.api.ApiAccount
import com.util.WxUtils
import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Transactional
class WxGroupService {
    def wxService

    def getWxGroup(ApiAccount apiAccount) {
        if (apiAccount) {
            wxService.token(apiAccount)
            def wxParam = [:]
            wxParam.put("access_token", apiAccount.apiToken.accessToken);
            def groupGet = WxUtils.doAll(WxUtils.WX_GROUP_GET_URL, wxParam, WxUtils.GET)
            def jSONObject = new JSONObject(groupGet)
            if (jSONObject.has("groups")) {
                def array = jSONObject.getJSONArray("groups")
                array?.each { JSONObject oneGroup ->
                    def wxId = oneGroup.getInt("id")
                    def name = oneGroup.getString("name")
                    def count = oneGroup.getInt("count")
                    def wxGroup = WxGroup.findByApiAccountAndWxId(apiAccount, wxId)
                    if (wxGroup) {
                        wxGroup.setName(name)
                        wxGroup.setCount(count)
                    } else {
                        new WxGroup(wxId: wxId, name: name, count: count, apiAccount: apiAccount).save(flush: true)
                    }
                }
            }
            return groupGet
        }
        return ""
    }

    def wxGroupUsersUpdate(List<WxUser> wxUsers, WxGroup toGroup, String accessToken) {
        List<WxUser> wxUsers50 = new ArrayList<>()
        Collection<Long> successIds = new ArrayList<>()
        wxUsers.eachWithIndex { WxUser entry, int i ->
            wxUsers50.add(entry)
            if ((i + 1) % 50 == 0) {
                println(wxUsers50)
                successIds.addAll(wxGroup50UsersUpdate(wxUsers50, toGroup, accessToken))
                wxUsers50 = new ArrayList<>()
            }
        }
        if (wxUsers50.size() > 0) {
            println(wxUsers50)
            successIds.addAll(wxGroup50UsersUpdate(wxUsers50, toGroup, accessToken))
        }
        if (successIds.size() > 0) {
            def criteria = WxUser.where {
                id in successIds
            }
            criteria.updateAll(groupid: toGroup.getWxId(), wxGroup: toGroup)
        }
        return ""
    }

    def wxGroup50UsersUpdate(List<WxUser> wxUsers, WxGroup toGroup, String accessToken) {
        Collection<Long> successIds = new ArrayList<>()
        def wxParam = [:]
        wxParam.put("access_token", accessToken);
        def postData = new JSONObject()
        def openidlist = new JSONArray()
        wxUsers.each {
            openidlist.put(it.openid)
        }
        postData.put("openid_list", openidlist)
        postData.put("to_groupid", toGroup.getWxId())
        log.info(postData.toString())
        def jSONObject = new JSONObject(WxUtils.doAll(WxUtils.WX_MEMBERS_BATCH_UPDATE_URL, wxParam, WxUtils.POST, postData))
        if (jSONObject.has("errcode") && jSONObject.getInt("errcode") == 0) {
            successIds.addAll(wxUsers.id)
        }
        return successIds
    }


    def wxGroupUserUpdate(WxUser wxUser, WxGroup toGroup) {
        def wxParam = [:]
        def apiAccount = wxUser.apiAccount
        wxParam.put("access_token", apiAccount.apiToken.accessToken);
        def postData = new JSONObject()
        postData.put("openid", wxUser.getOpenid())
        postData.put("to_groupid", toGroup.getWxId())
        def jSONObject = new JSONObject(WxUtils.doAll(WxUtils.WX_MEMBERS_UPDATE_URL, wxParam, WxUtils.POST, postData))
        if (jSONObject.has("errcode") && jSONObject.getInt("errcode") == 0) {
            wxUser.setGroupid(toGroup.getWxId())
            wxUser.setWxGroup(toGroup)
            wxUser.save(flush: true)
        }
        return jSONObject

    }

    def createWxGroup(WxGroup wxGroup) {
        def wxParam = [:]
        def apiAccount = wxGroup.apiAccount
        wxParam.put("access_token", apiAccount.apiToken.accessToken);
        def postData = new JSONObject()
        def group = new JSONObject()
        group.put("id", wxGroup.getWxId())
        group.put("name", wxGroup.getName())
        postData.put("group", group)
        def jSONObject = new JSONObject(WxUtils.doAll(WxUtils.WX_GROUP_CREATE_URL, wxParam, WxUtils.POST, postData))
        return jSONObject
    }


    int batchMove(WxGroup fromGroup, WxGroup toGroup, String accessToken) {
        int limit = 50;
        Integer total = WxUser.countByWxGroup(fromGroup)
        int page = total / limit;
        if (total % limit > 0) {
            page++;
        }
        Collection<Long> successIds = new ArrayList<>()
        for (int i = 0; i < page; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("max", limit);
            map.put("offset", limit * i);
            map.put("sort", "id");
            List<WxUser> wxUserList = WxUser.findAllByWxGroup(fromGroup, map);
            successIds.addAll(wxGroup50UsersUpdate(wxUserList, toGroup, accessToken))
        }
        if (successIds.size() > 0) {
            def criteria = WxUser.where {
                id in successIds
            }
            criteria.updateAll(groupid: toGroup.getWxId(), wxGroup: toGroup)
        }
        return 0;
    }

    def moveToNewGroup(ApiAccount apiAccount,String accessToken) {
        def fromWxGroup = WxGroup.findByNameAndApiAccount("未分组", apiAccount)
        def toWxGroup = WxGroup.findByNameAndApiAccount("新用户", apiAccount)
        batchMove(fromWxGroup, toWxGroup, accessToken)

    }

    def moveToOldGroup(ApiAccount apiAccount,String token) {
        def openidList = NpWxUser.findAll().openid
        def wxGroup = WxGroup.findByNameAndApiAccount("老用户", apiAccount)
        def wxUsers = WxUser.findAllByUnionidInListAndWxGroupNotEqual(openidList, wxGroup)
        log.info("#####moveToOldGroup.wxUsers.size=" + wxUsers.size())
        wxGroupUsersUpdate(wxUsers, wxGroup, token)
    }


}
