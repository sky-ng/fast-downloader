package com.sky.fastdownloader.concurrent;

import com.sky.fastdownloader.utils.FileUtil;
import com.sky.fastdownloader.utils.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

public class DownloadHandler implements Runnable {

    private CountDownLatch countDownLatch;

    private String url;

    private File file;

    private long startPos;

    private long endPos;

    public DownloadHandler(CountDownLatch countDownLatch, String url, File file, long startPos, long endPos) {
        this.countDownLatch = countDownLatch;
        this.url = url;
        this.file = file;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = HttpUtil.getHttpConnection(url, startPos, endPos);
            FileUtil.createFile(connection.getInputStream(), file);
            HttpUtil.closeConnection(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        countDownLatch.countDown();
    }
}
