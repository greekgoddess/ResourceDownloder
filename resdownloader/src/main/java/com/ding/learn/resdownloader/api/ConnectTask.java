package com.ding.learn.resdownloader.api;

/**
 * Created by jindingwei on 2019/7/6.
 */

public interface ConnectTask extends Runnable {

    interface OnConnectListener {

        void onConnecting();

        void onConnected(long contentLength, boolean isAllowRang);

        void onPaused();

        void onCancled();

        void onConnectFailed();
    }

    void pause();

    void cancle();

    boolean isPaused();

    boolean isCancled();
}
