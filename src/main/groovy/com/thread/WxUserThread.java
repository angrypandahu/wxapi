package com.thread;

import com.abstractpackage.thread.AbstractThread;
import com.domain.wechat.WxUser;
import com.domain.wechat.WxUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WxUserThread extends AbstractThread {

    private List<WxUser> wxUserList;
    private WxUserService wxUserService;



    public WxUserThread(CountDownLatch mStartSignal, CountDownLatch mDoneSignal, int mThreadIndex, List<WxUser> wxUserList, WxUserService wxUserService) {
        super(mStartSignal, mDoneSignal, mThreadIndex);
        this.wxUserService = wxUserService;
        this.wxUserList = wxUserList;

    }

    public void doWork() {
        batchSaveThread(wxUserList);
    }

    private void batchSaveThread(List<WxUser> wxUserList) {
        List<WxUser> oneList = new ArrayList<>();
        for (WxUser wxUser : wxUserList) {
            oneList.add(wxUser);
            if (oneList.size() % 200 == 0) {
                List<WxUser> threadList = new ArrayList<>();
                threadList.addAll(oneList);
                oneList = new ArrayList<>();
                wxUserService.batchSave(threadList);

            }
        }
        if (oneList.size() > 0) {
            wxUserService.batchSave(oneList);
        }


    }
}
