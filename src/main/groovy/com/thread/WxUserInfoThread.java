package com.thread;

import com.abstractpackage.thread.AbstractThread;
import com.domain.wechat.WxUser;
import com.domain.wechat.WxUserService;
import com.util.WxUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.grails.web.json.JSONObject;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class WxUserInfoThread extends AbstractThread {
    private List<WxUser> wxUserList;
    private WxUserService wxUserService;
    private boolean withNewTransaction;
    private static final Log log = LogFactory.getLog(WxUserInfoThread.class);

    public WxUserInfoThread(CountDownLatch mStartSignal, CountDownLatch mDoneSignal, int mThreadIndex, List<WxUser> wxUserList, WxUserService wxUserService,boolean withNewTransaction) {
        super(mStartSignal, mDoneSignal, mThreadIndex);
        this.wxUserService = wxUserService;
        this.wxUserList = wxUserList;
        this.withNewTransaction = withNewTransaction;
    }

    @Override
    public void doWork() {
        log.info("WxUserInfoThread##############doWork##############Start->wxUserList.size=" + wxUserList.size());
        for (WxUser wxUser : wxUserList) {
            String userInfo = wxUserService.userInfo(wxUser);
            JSONObject jSONObject = new JSONObject(userInfo);
            wxUserService.userInfo(jSONObject, wxUser);
        }
        wxUserService.batchUpdate(wxUserList,withNewTransaction);
        log.info("WxUserInfoThread##############doWork##############End");

    }
}
