package com.batch;

import com.abstractpackage.thread.AbstractBatch;
import com.domain.wechat.WxUser;
import com.domain.wechat.WxUserService;
import com.thread.WxUserInfoThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WxBatchGetUserInfo extends AbstractBatch {
    private List<WxUser> wxUserList;
    private WxUserService wxUserService;
    private boolean withNewTransaction;

    private List<WxUser> getThreadList(List<WxUser> totalList, int start, int limitNum) {
        List<WxUser> threadList = new ArrayList<>();
        for (int i = 0; i < totalList.size(); i++) {
            if (i >= start && i < start + limitNum) {
                threadList.add(totalList.get(i));
            }
        }
        return threadList;
    }


    public WxBatchGetUserInfo(List<WxUser> wxUserList, WxUserService wxUserService,boolean withNewTransaction) {
        this.wxUserList = wxUserList;
        this.wxUserService = wxUserService;
        this.withNewTransaction = withNewTransaction;
    }

    @Override
    public void initThreadList(CountDownLatch mStartSignal, CountDownLatch mDoneSignal, int threadIndex, int startNum, int limitNum) {
        List<WxUser> threadList = getThreadList(wxUserList, (startNum + threadIndex) * getLimitNum(), getLimitNum());
        WxUserInfoThread wxUserInfoThread = new WxUserInfoThread(mStartSignal, mDoneSignal, threadIndex, threadList, wxUserService,withNewTransaction);
        wxUserInfoThread.start();
    }
}
