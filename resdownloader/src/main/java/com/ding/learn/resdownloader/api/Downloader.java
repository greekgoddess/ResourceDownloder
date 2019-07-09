package com.ding.learn.resdownloader.api;

/**
 * Created by jindingwei on 2019/7/6.
 */

public interface Downloader {

    boolean isRuning();

    boolean isPaused();

    void start();

    void pause();

    void cancle();
}
