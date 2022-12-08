package com.sky.fastdownloader.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

}
