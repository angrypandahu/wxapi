package com.utils

import com.batch.WxBatchGetUser
import com.batch.WxBatchGetUserInfo
import com.domain.api.ApiAccount
import com.domain.api.WxService
import com.domain.wechat.WxUser
import com.domain.wechat.WxUserService
import com.util.WxUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

/**
 * Created by hupanpan on 2017/4/6.
 *
 */
class WxSyncUtils {
    WxService wxService
    WxUserService wxUserService


    int getWxUsers(ApiAccount apiAccount, String nextOpenId) {

        String users = wxService.userGet(apiAccount, nextOpenId)
        int count = 0
        if (users != null && users != "") {
            JSONObject jsonObject = new JSONObject(users)
            if (jsonObject.has("data")) {
                count = jsonObject.getInt("count")
                nextOpenId = jsonObject.getString("next_openid")
                JSONArray openidArray = jsonObject.getJSONObject("data").getJSONArray("openid")
                List<WxUser> list = new ArrayList<>()
                for (int i = 0; i < count; i++) {
                    String openId = openidArray.getString(i)
                    WxUser wxUser = new WxUser()
                    wxUser.setApiAccount(apiAccount)
                    wxUser.setOpenid(openId)
                    list.add(wxUser)
                }
                new WxBatchGetUser(list, wxUserService).batch(list.size())
                def detachedCriteria = WxUser.where {
                    (apiAccount == apiAccount) && (openid in openidArray)
                }
                detachedCriteria.updateAll(isDelete: false)
                if (nextOpenId != null && nextOpenId != "") {
                    getWxUsers(apiAccount, nextOpenId)
                }
            }

        }
        return count
    }

    def getWxUserInfo(ApiAccount apiAccount) {
        int limit = 1000;
        Integer total = WxUser.countByApiAccountAndUnionidIsNull(apiAccount)
        int page = total / limit;
        if (total % limit > 0) {
            page++;
        }
        for (int i = 0; i < page; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("max", limit);
            map.put("offset", limit * i);
            map.put("sort", "id");
            List<WxUser> wxUserList = WxUser.findAllByApiAccountAndUnionidIsNull(apiAccount, map);
            new WxBatchGetUserInfo(wxUserList, wxUserService).batch(wxUserList.size())
        }
    }

    def getAllWxUsers(ApiAccount apiAccount) {
        def criteria = WxUser.where {
            apiAccount == apiAccount
        }
        criteria.updateAll(isDelete: true)

        getWxUsers(apiAccount, "")
    }


}
