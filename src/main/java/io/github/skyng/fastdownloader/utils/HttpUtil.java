package io.github.skyng.fastdownloader.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    private static final Map<String, String> basicRequestHeader;

    static {
        basicRequestHeader = new HashMap<>();
        basicRequestHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
    }

    // 获取请求连接
    public static HttpURLConnection getHttpConnection(String httpUrl) throws IOException {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 给请求加上请求头
        for (Map.Entry<String, String> entry : basicRequestHeader.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return connection;
    }

    // 获取指定字节范围内的请求连接
    public static HttpURLConnection getHttpConnection(String httpUrl, Long startPos, Long endPos) throws IOException {
        HttpURLConnection connection = getHttpConnection(httpUrl);
        connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
        return connection;
    }

    // 获取请求资源的大小
    public static long getResourceLength(HttpURLConnection connection) {
        return connection.getContentLengthLong();
    }

    // 不再使用时及时关闭连接
    public static void closeConnection(HttpURLConnection connection) {
        connection.disconnect();
    }
}
