package com.ding.learn.resdownloader.api;

/**
 * Created by jindingwei on 2019/7/6.
 */

public interface DownloadTask extends Runnable {

    interface OnDownloadListener {

        void onProgressUpdate(long finished, long total, float percent);

        void onCompleted();

        void onPaused();

        void onCancled();

        void onFailed();
    }

    void resume();

    void pause();

    void cancle();

    boolean isPaused();

    boolean isCancled();

    boolean isFailed();

    boolean isRuning();

    boolean isCompleted();
}
