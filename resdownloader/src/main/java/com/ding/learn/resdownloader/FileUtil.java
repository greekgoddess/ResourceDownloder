package com.ding.learn.resdownloader;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by jindingwei on 2019/7/7.
 */

public class FileUtil {

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
