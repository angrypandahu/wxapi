package com.utils;

import com.batch.WxBatchGetUser;
import com.domain.api.ApiAccount;
import com.domain.api.WxService;
import com.domain.wechat.WxUser;
import com.domain.wechat.WxUserService;
import org.grails.web.json.JSONArray;
import org.grails.web.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WxBatchSaveUtils {


    public static int getWxUsers(ApiAccount apiAccount, String nextOpenId, WxService wxService, WxUserService wxUserService) {

        String users = wxService.userGet(apiAccount, nextOpenId);
        int count = 0;
        if (users != null && !users.equals("")) {
            JSONObject jsonObject = new JSONObject(users);
            if (jsonObject.has("data")) {
                count = jsonObject.getInt("count");
                nextOpenId = jsonObject.getString("next_openid");
                JSONArray openidArray = jsonObject.getJSONObject("data").getJSONArray("openid");
                List<WxUser> list = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    String openId = openidArray.getString(i);
                    WxUser wxUser = new WxUser();
                    wxUser.setApiAccount(apiAccount);
                    wxUser.setOpenid(openId);
                    list.add(wxUser);
                }
                new WxBatchGetUser(list, wxUserService).batch(list.size());
                if (nextOpenId != null && !nextOpenId.equals("")) {
                    getWxUsers(apiAccount, nextOpenId, wxService, wxUserService);
                }
            }

        }
        return count;
    }


}
