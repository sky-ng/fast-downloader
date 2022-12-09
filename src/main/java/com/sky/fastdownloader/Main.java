package com.sky.fastdownloader;

import com.sky.fastdownloader.concurrent.DownloadHandler;
import com.sky.fastdownloader.utils.CommonConstants;
import com.sky.fastdownloader.utils.FileUtil;
import com.sky.fastdownloader.utils.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("please input url!");
        } else if (args.length > 1) {
            System.err.println("only one url should be input!");
        }
        if (!checkUrl(args[0])) {
            System.err.println("url is invalid!");
        }
        try {
            download(args[0]);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private static void download(String url) throws IOException, InterruptedException {
        HttpURLConnection connection = HttpUtil.getHttpConnection(url);
        long resourceLength = HttpUtil.getResourceLength(connection);
        String resourceName = getResourceName(url);
        if (resourceLength <= CommonConstants.FILE_SIZE_THRESHOLD) {
            // 单线程下载
            File file = new File(resourceName);
            FileUtil.createFile(connection.getInputStream(), file);
            HttpUtil.closeConnection(connection);
        } else {
            // 多线程下载
            HttpUtil.closeConnection(connection);
            Executor executor = Executors.newFixedThreadPool(CommonConstants.THREAD_SIZE);
            CountDownLatch countDownLatch = new CountDownLatch(CommonConstants.THREAD_SIZE);
            int singleSize = (int) Math.ceil(resourceLength * 1.0 / CommonConstants.THREAD_SIZE);
            List<File> tmpFileList = new ArrayList<>();
            for (int i = 0; i < CommonConstants.THREAD_SIZE; i++) {
                int startPos = i * singleSize;
                int endPos = (i + 1) * singleSize - 1;
                File tmpFile = new File(resourceName + ".tmp" + i);
                tmpFileList.add(tmpFile);
                if (endPos <= resourceLength) {
                    executor.execute(new DownloadHandler(countDownLatch, url, tmpFile, startPos, endPos));
                } else {
                    executor.execute(new DownloadHandler(countDownLatch, url, tmpFile, startPos, resourceLength));
                }
            }
            countDownLatch.await();
            FileUtil.compositeFile(tmpFileList, resourceName);
        }

    }

    // 根据url获取资源名称
    private static String getResourceName(String url) {
        String[] splitUrl = url.split("/");
        return splitUrl[splitUrl.length - 1];
    }
}