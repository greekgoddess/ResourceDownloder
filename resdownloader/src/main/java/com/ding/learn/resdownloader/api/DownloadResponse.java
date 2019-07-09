package com.ding.learn.resdownloader.api;

/**
 * Created by jindingwei on 2019/7/6.
 */

public interface DownloadResponse {

    void onStarted();

    void onConnecting();

    void onConnected(long length, boolean isAllowRang);

    void onProgressUpdate(long finished, long total, float percent);

    void onCompleted();

    void onPaused();

    void onCancled();

    void onFailed();
}
