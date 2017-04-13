package com.abstractpackage.thread;

import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractThread extends Thread {
    private CountDownLatch mStartSignal;
    private CountDownLatch mDoneSignal;
    private int mThreadIndex;
    private final static Logger log = Logger.getLogger(AbstractThread.class);

    public abstract void doWork();


    @Override
    public void run() {
        try {
            mStartSignal.await();//
            log.info("###########Tread->" + mThreadIndex + "###########StartDoWork");
            try {
                doWork();//
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            log.info("###########Tread->" + mThreadIndex + "###########EndDoWork");
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
