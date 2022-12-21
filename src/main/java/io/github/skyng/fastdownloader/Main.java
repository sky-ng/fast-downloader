package io.github.skyng.fastdownloader;

import io.github.skyng.fastdownloader.concurrent.DownloadHandler;
import io.github.skyng.fastdownloader.utils.CommonConstants;
import io.github.skyng.fastdownloader.utils.FileUtil;
import io.github.skyng.fastdownloader.utils.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("please input url!");
            return;
        } else if (args.length > 1) {
            System.err.println("only one url should be input!");
            return;
        }
        if (!checkUrl(args[0])) {
            System.err.println("url is invalid!");
            return;
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
        HttpUtil.closeConnection(connection);
        String resourceName = getResourceName(url);
        if (resourceLength <= CommonConstants.FILE_SIZE_THRESHOLD) {
            // 单线程下载
            File file = new File(resourceName);
            if (file.exists() && file.length() == resourceLength) {
                System.err.println("file has been downloaded!");
                return;
            }
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.execute(new DownloadHandler(url, file, 0, resourceLength));
            es.shutdown();
        } else {
            // 多线程下载
            if (FileUtil.exist(resourceName)) {
                System.err.println("file has been downloaded!");
                return;
            }
            ExecutorService es = Executors.newFixedThreadPool(CommonConstants.THREAD_SIZE);
            CountDownLatch countDownLatch = new CountDownLatch(CommonConstants.THREAD_SIZE);
            int singleSize = (int) Math.ceil(resourceLength * 1.0 / CommonConstants.THREAD_SIZE);
            List<File> tmpFileList = new ArrayList<>();
            for (int i = 0; i < CommonConstants.THREAD_SIZE; i++) {
                int startPos = i * singleSize;
                int endPos = (i + 1) * singleSize - 1;
                File tmpFile = new File(resourceName + ".tmp" + i);
                tmpFileList.add(tmpFile);
                if (endPos <= resourceLength) {
                    es.execute(new DownloadHandler(countDownLatch, url, tmpFile, startPos, endPos));
                } else {
                    es.execute(new DownloadHandler(countDownLatch, url, tmpFile, startPos, resourceLength));
                }
            }
            countDownLatch.await();
            FileUtil.compositeFile(tmpFileList, resourceName);
            es.shutdown();
        }

    }

    // 根据url获取资源名称
    private static String getResourceName(String url) throws UnsupportedEncodingException {
        String[] splitUrl = url.split("/");
        String resourceName = URLDecoder.decode(splitUrl[splitUrl.length - 1], "utf-8");
        // 以下处理url中有参数的情况
        int index = resourceName.indexOf("?");
        if (index != -1) {
            resourceName = resourceName.substring(0, index);
        }
        return resourceName;
    }
}