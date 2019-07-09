package com.ding.learn.resdownloader.core;

import com.ding.learn.resdownloader.DownloadInfo;
import com.ding.learn.resdownloader.DownloadRequest;
import com.ding.learn.resdownloader.SubDownloadInfo;
import com.ding.learn.resdownloader.db.DataBaseManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

/**
 * Created by jindingwei on 2019/7/7.
 */

public class MultiThreadDownloadTask extends AbstractDownloadTaskImpl {

    public MultiThreadDownloadTask(DownloadRequest request, DownloadInfo downloadInfo,
                                   SubDownloadInfo subDownloadInfo, OnDownloadListener listener) {
        super(request, downloadInfo, subDownloadInfo, listener);
    }

    @Override
    public void addHttpHeader(HttpURLConnection connection) {
        if (connection != null) {
            long start = mSubDownloadInfo.getStart() + mSubDownloadInfo.getFinished();
            connection.setRequestProperty("Range", "bytes=" + start + "-" + mSubDownloadInfo.getEnd());
        }
    }

    @Override
    public void insertIntoDB() {
        if (!DataBaseManager.getInstance().exist(mSubDownloadInfo.tag, mSubDownloadInfo.getTaskId())) {
            DataBaseManager.getInstance().insert(mSubDownloadInfo);
        }
    }

    @Override
    public void updateDB(long finished) {
        DataBaseManager.getInstance().updateFinished(mSubDownloadInfo.tag, mSubDownloadInfo.getTaskId(), finished);
    }

    @Override
    public int getHttpResponseCode() {
        return HttpURLConnection.HTTP_PARTIAL;
    }

    @Override
    public RandomAccessFile getFile(File dir, String name) throws IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(mSubDownloadInfo.getStart() + mSubDownloadInfo.getFinished());
        return raf;
    }
}
