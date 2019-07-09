package com.ding.learn.resdownloader.core;

import android.text.TextUtils;

import com.ding.learn.resdownloader.DownloadConfig;
import com.ding.learn.resdownloader.DownloadRequest;
import com.ding.learn.resdownloader.DownloadStatus;
import com.ding.learn.resdownloader.api.ConnectTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class ConnectTaskImpl implements ConnectTask {
    private DownloadRequest mRequest;
    private OnConnectListener mListener;
    private volatile int mStatus;
    private volatile int mCommand;

    public ConnectTaskImpl(DownloadRequest request, OnConnectListener listener) {
        mRequest = request;
        mListener = listener;
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
    public boolean isCancled() {
        return mStatus == DownloadStatus.DOWNLOAD_CANCLED;
    }

    @Override
    public void run() {
        if (checkPausedAndCancle()) {
            return;
        }

        mStatus = DownloadStatus.DOWNLOAD_CONNECTING;
        mListener.onConnecting();

        HttpURLConnection connection = null;
        try {
            URL url = new URL(mRequest.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DownloadConfig.HTTP_CONNECT_TIME_OUT);
            connection.setReadTimeout(DownloadConfig.HTTP_READ_TIME_OUT);
            connection.setRequestMethod(DownloadConfig.HTTP_GET);
            connection.setRequestProperty("Range", "bytes=0-");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                parserResponsed(connection, false);
            } else if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                parserResponsed(connection, true);
            } else {
                connectFailed();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            connectFailed();
        } catch (IOException e) {
            e.printStackTrace();
            connectFailed();
        } catch (Exception e) {
            e.printStackTrace();
            connectFailed();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void parserResponsed(HttpURLConnection connection, boolean isAllowRang) throws Exception {
        if (connection == null) {
            throw new Exception("Connect Failed");
        }
        long length = 0;
        String contentLen = connection.getHeaderField("Content_Length");
        if (!TextUtils.isEmpty(contentLen)) {
            length = Long.parseLong(contentLen);
        }
        if (length <= 0) {
            length = connection.getContentLength();
        }
        if (!checkPausedAndCancle()) {
            mStatus = DownloadStatus.DOWNLOAD_CANCLED;
            mListener.onConnected(length, isAllowRang);
        }
    }

    private void connectFailed() {
        mStatus = DownloadStatus.DOWNLOAD_FAILED;
        mListener.onConnectFailed();
    }

    private boolean checkPausedAndCancle() {
        if (mCommand == DownloadStatus.DOWNLOAD_CANCLED) {
            mStatus = DownloadStatus.DOWNLOAD_CANCLED;
            mListener.onCancled();
            return true;
        } else if (mCommand == DownloadStatus.DOWNLOAD_PAUSED) {
            mStatus = DownloadStatus.DOWNLOAD_PAUSED;
            mListener.onPaused();
            return true;
        }
        return false;
    }
}
