package com.sky.fastdownloader.concurrent;

import java.util.concurrent.CountDownLatch;

public class DownloadHandler implements Runnable {

    private CountDownLatch countDownLatch;


    @Override
    public void run() {

        countDownLatch.countDown();
    }
}
