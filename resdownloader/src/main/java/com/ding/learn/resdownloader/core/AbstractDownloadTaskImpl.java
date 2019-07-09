package com.ding.learn.resdownloader.core;

import com.ding.learn.resdownloader.DownloadConfig;
import com.ding.learn.resdownloader.DownloadInfo;
import com.ding.learn.resdownloader.DownloadRequest;
import com.ding.learn.resdownloader.DownloadStatus;
import com.ding.learn.resdownloader.SubDownloadInfo;
import com.ding.learn.resdownloader.api.DownloadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jindingwei on 2019/7/6.
 */

public abstract class AbstractDownloadTaskImpl implements DownloadTask {

    private volatile int mStatus;
    private volatile int mCommand;
    private OnDownloadListener mListener;
    private DownloadInfo mDownloadInfo;
    protected SubDownloadInfo mSubDownloadInfo;
    private DownloadRequest mRequest;

    public AbstractDownloadTaskImpl(DownloadRequest request, DownloadInfo downloadInfo,
                                    SubDownloadInfo subDownloadInfo, OnDownloadListener listener) {
        mListener = listener;
        mDownloadInfo = downloadInfo;
        mSubDownloadInfo = subDownloadInfo;
        mRequest = request;
    }

    @Override
    public void resume() {
        if (mCommand == DownloadStatus.DOWNLOAD_PAUSED) {
            mCommand = DownloadStatus.DOWNLOAD_START;
        }
    }

    @Override
    public void pause() {
        mCommand = DownloadStatus.DOWNLOAD_PAUSED;
    }

    @Override
    public void cancle() {
        mCommand = DownloadStatus.DOWNLOAD_CANCLED;
    }

    @Override
    public boolean isPaused() {
        return mStatus == DownloadStatus.DOWNLOAD_PAUSED;
    }

    @Override
    public boolean isFailed() {
        return mStatus == DownloadStatus.DOWNLOAD_FAILED;
    }

    @Override
    public boolean isCancled() {
        return mStatus == DownloadStatus.DOWNLOAD_CANCLED;
    }

    @Override
    public boolean isCompleted() {
        return mStatus == DownloadStatus.DOWNLOAD_COMPLETED;
    }

    @Override
    public boolean isRuning() {
        return mStatus == DownloadStatus.DOWNLOAD_PROGRESS;
    }

    @Override
    public void run() {
        mStatus = DownloadStatus.DOWNLOAD_PROGRESS;
        insertIntoDB();

        if (checkPausedAndCancle()) {
            return;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(mRequest.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DownloadConfig.HTTP_CONNECT_TIME_OUT);
            connection.setReadTimeout(DownloadConfig.HTTP_READ_TIME_OUT);
            addHttpHeader(connection);
            connection.setRequestMethod(DownloadConfig.HTTP_GET);
            connection.connect();
            if (connection.getResponseCode() == getHttpResponseCode()) {
                executeDownload(connection);
                updateDB(mSubDownloadInfo.getFinished());
                if (mStatus == DownloadStatus.DOWNLOAD_PROGRESS) {
                    synchronized (mListener) {
                        mStatus = DownloadStatus.DOWNLOAD_COMPLETED;
                        mListener.onCompleted();
                    }
                }
            } else {
                downloadFailed();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            downloadFailed();
        } catch (IOException e) {
            e.printStackTrace();
            downloadFailed();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void executeDownload(HttpURLConnection connection) throws IOException {
        InputStream ins = null;
        RandomAccessFile accessFile = null;
        try {
            ins = connection.getInputStream();
            accessFile = getFile(mDownloadInfo.getDir(), mDownloadInfo.getName());
            byte[] buffer = new byte[2 * 1024];
            while (true) {
                if (checkPausedAndCancle()) {
                    return;
                }
                int len = ins.read(buffer);
                if (len < 0) {
                    break;
                }
                accessFile.write(buffer, 0, len);
                mSubDownloadInfo.setFinished(mSubDownloadInfo.getFinished() + len);
                synchronized (mListener) {
                    mDownloadInfo.setFinishedLength(mDownloadInfo.getFinishedLength() + len);
                    float percent = ((float) mDownloadInfo.getFinishedLength()) / mDownloadInfo.getTotalLength();
                    mListener.onProgressUpdate(mDownloadInfo.getFinishedLength(), mDownloadInfo.getTotalLength(), percent);
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
                if (accessFile != null) {
                    accessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                downloadFailed();
            }
        }
    }

    public abstract void addHttpHeader(HttpURLConnection connection);

    public abstract int getHttpResponseCode();

    public abstract RandomAccessFile getFile(File dir, String name) throws IOException;

    public abstract void insertIntoDB();

    public abstract void updateDB(long finished);

    protected void downloadFailed() {
        updateDB(mSubDownloadInfo.getFinished());
        mStatus = DownloadStatus.DOWNLOAD_FAILED;
        synchronized (mListener) {
            mListener.onFailed();
        }
    }

    private boolean checkPausedAndCancle() {
        if (mCommand == DownloadStatus.DOWNLOAD_CANCLED) {
            synchronized (mListener) {
                mStatus = DownloadStatus.DOWNLOAD_CANCLED;
                mListener.onCancled();
            }
            return true;
        } else if (mCommand == DownloadStatus.DOWNLOAD_PAUSED) {
            synchronized (mListener) {
                mStatus = DownloadStatus.DOWNLOAD_PAUSED;
                mListener.onPaused();
            }
            return true;
        }
        return false;
    }
}
