package com.abstractpackage.thread;


import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractBatch {

    public abstract void initThreadList(CountDownLatch mStartSignal, CountDownLatch mDoneSignal, int threadIndex, int startNum, int limitNum);

    private int limitNum = 200;

    protected int getLimitNum() {
        return limitNum;
    }

    protected void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    private final static Logger log = Logger.getLogger(AbstractBatch.class);


    public boolean batch(int total) {
        int threadNum = total / limitNum;
        if (total % limitNum > 0) {
            threadNum++;
        }
        int i = 0;
        while (true) {
            if (threadNum < 0) {
                break;
            } else {
                int oneThreadNum = threadNum > 5 ? 5 : threadNum;
                threadDoWork(i, oneThreadNum);
                i += oneThreadNum;
                threadNum -= 5;
            }
        }
        return true;
    }


    private boolean threadDoWork(int startNum, int threadNum) {
        java.util.Date begin = new java.util.Date();
        log.info("Main Thread Now:" + begin);
        final CountDownLatch mStartSignal = new CountDownLatch(1);
        final CountDownLatch mDoneSignal = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; i++) {
            initThreadList(mStartSignal, mDoneSignal, i, startNum, getLimitNum());
        }
//        doPrepareWork();//
        mStartSignal.countDown();//
//        doSomethingElse();//
        try {
            mDoneSignal.await();//
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }
        log.info("All workers have finished now.");
        java.util.Date end = new java.util.Date();
        log.info("Main Thread Now:" + end);
        long l = (end.getTime() - begin.getTime()) / 1000;
        log.info("Total time: " + l);
        return true;
    }
}
