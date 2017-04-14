package com.abstractpackage.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractThread extends Thread {
    private CountDownLatch mStartSignal;
    private CountDownLatch mDoneSignal;
    private int mThreadIndex;
    private static final Log log = LogFactory.getLog(AbstractThread.class);

    public abstract void doWork();


    @Override
    public void run() {
        try {
            mStartSignal.await();//
            log.debug("###########Tread->" + mThreadIndex + "###########StartDoWork");
            try {
                doWork();//
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            log.debug("###########Tread->" + mThreadIndex + "###########EndDoWork");
            mDoneSignal.countDown();//
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public AbstractThread(CountDownLatch mStartSignal, CountDownLatch mDoneSignal, int mThreadIndex) {
        this.mStartSignal = mStartSignal;
        this.mDoneSignal = mDoneSignal;
        this.mThreadIndex = mThreadIndex;

    }


}
