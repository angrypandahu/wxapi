package com.batch;

import com.abstractpackage.thread.AbstractBatch;
import com.domain.wechat.WxUser;
import com.domain.wechat.WxUserService;
import com.thread.WxUserThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WxBatchGetUser extends AbstractBatch {
    private List<WxUser> wxUserList;
    private WxUserService wxUserService;

    private List<WxUser> getThreadList(List<WxUser> totalList, int start, int limitNum) {
        List<WxUser> threadList = new ArrayList<>();
        for (int i = 0; i < totalList.size(); i++) {
            if (i >= start && i < start + limitNum) {
                threadList.add(totalList.get(i));
            }
        }
        return threadList;
    }


    public WxBatchGetUser(List<WxUser> wxUserList, WxUserService wxUserService) {
        this.wxUserList = wxUserList;
        this.wxUserService = wxUserService;
    }

    @Override
    public void initThreadList(CountDownLatch mStartSignal, CountDownLatch mDoneSignal, int threadIndex, int startNum, int limitNum) {
        int myLimit = 2000;
        this.setLimitNum(myLimit);
        List<WxUser> threadList = getThreadList(wxUserList, (startNum + threadIndex) * getLimitNum(), getLimitNum());
        WxUserThread wxUserThread = new WxUserThread(mStartSignal, mDoneSignal, threadIndex, threadList, wxUserService);
        wxUserThread.start();
    }
}
