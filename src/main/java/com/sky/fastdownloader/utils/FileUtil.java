package com.sky.fastdownloader.utils;

import java.io.*;
import java.util.List;

public class FileUtil {

    // 根据传入的流生成一个文件
    public static void createFile(InputStream is, File file) throws IOException {
        byte[] buffer = new byte[CommonConstants.BUF_SIZE];
        FileOutputStream os = new FileOutputStream(file);
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }
        file.mkdirs();
        os.close();
    }

    // 将多个临时文件拼成一个完整的文件
    public static void compositeFile(List<File> tmpFileList, String resourceName) throws IOException {
        byte[] buffer = new byte[CommonConstants.BUF_SIZE];
        File file = new File(resourceName);
        FileOutputStream os = new FileOutputStream(file, true);
        for (File tmpFile : tmpFileList) {
            FileInputStream is = new FileInputStream(tmpFile);
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            is.close();
            tmpFile.delete();
        }
        file.mkdirs();
        os.close();
    }
}
