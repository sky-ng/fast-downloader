package com.sky.fastdownloader.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    private static HttpURLConnection connection;

    private static Map<String, String> requestHeader;

    static {
        requestHeader = new HashMap<>();
        requestHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
    }

    public static HttpURLConnection getHttpConnection(String httpUrl) throws IOException {
        URL url = new URL(httpUrl);
        connection = (HttpURLConnection) url.openConnection();
        // 给请求加上请求头
        for (Map.Entry<String, String> entry : requestHeader.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return connection;
    }

}
