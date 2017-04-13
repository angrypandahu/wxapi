package com.thread;

import com.abstractpackage.thread.AbstractThread;
import com.domain.wechat.WxUser;
import com.domain.wechat.WxUserService;
import com.util.WxUtils;
import org.apache.log4j.Logger;
import org.grails.web.json.JSONObject;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class WxUserInfoThread extends AbstractThread {
    private List<WxUser> wxUserList;
    private WxUserService wxUserService;
    private final static Logger log = Logger.getLogger(WxUserInfoThread.class);

    public WxUserInfoThread(CountDownLatch mStartSignal, CountDownLatch mDoneSignal, int mThreadIndex, List<WxUser> wxUserList, WxUserService wxUserService) {
        super(mStartSignal, mDoneSignal, mThreadIndex);
        this.wxUserService = wxUserService;
        this.wxUserList = wxUserList;
    }

    @Override
    public void doWork() {
        log.info("WxUserInfoThread##############doWork##############Start->wxUserList.size=" + wxUserList.size());
        for (WxUser wxUser : wxUserList) {
            String userInfo = WxUtils.userInfo(wxUser);
            JSONObject jSONObject = new JSONObject(userInfo);
            wxUserService.userInfo(jSONObject, wxUser);
        }
        wxUserService.batchUpdate(wxUserList);
        log.info("WxUserInfoThread##############doWork##############End");

    }
}
