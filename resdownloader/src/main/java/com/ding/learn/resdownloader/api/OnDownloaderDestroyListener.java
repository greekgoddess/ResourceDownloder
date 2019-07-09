package com.ding.learn.resdownloader.api;


import com.ding.learn.resdownloader.DownloadRequest;

/**
 * Created by jindingwei on 2019/7/7.
 */

public interface OnDownloaderDestroyListener {

    void onDestroy(DownloadRequest key, Downloader downloader);
}
