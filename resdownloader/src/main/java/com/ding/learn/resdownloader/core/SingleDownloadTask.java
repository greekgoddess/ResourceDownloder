package com.ding.learn.resdownloader.core;


import com.ding.learn.resdownloader.DownloadInfo;
import com.ding.learn.resdownloader.DownloadRequest;
import com.ding.learn.resdownloader.SubDownloadInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class SingleDownloadTask extends AbstractDownloadTaskImpl {

    public SingleDownloadTask(DownloadRequest request, DownloadInfo downloadInfo,
                              SubDownloadInfo subDownloadInfo, OnDownloadListener listener) {
        super(request, downloadInfo, subDownloadInfo, listener);
    }

    @Override
    public void addHttpHeader(HttpURLConnection connection) {

    }

    @Override
    public void insertIntoDB() {

    }

    @Override
    public void updateDB(long finished) {

    }

    @Override
    public int getHttpResponseCode() {
        return HttpURLConnection.HTTP_OK;
    }

    @Override
    public RandomAccessFile getFile(File dir, String name) throws FileNotFoundException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        return raf;
    }
}
