package com.ding.learn.resdownloader.api;

/**
 * Created by jindingwei on 2019/7/6.
 */

public interface DownloadCallBack {

    void onStarted();

    void onConnecting();

    void onConnected(long length, boolean isAllowRang);

    void onProgressUpdate(long finished, long total, float percent);

    void onCompleted();

    void onCancled();

    void onPaused();

    void onFailed();
}
