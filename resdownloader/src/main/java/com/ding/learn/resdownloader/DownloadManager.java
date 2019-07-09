package com.ding.learn.resdownloader;

import android.content.Context;

import com.ding.learn.resdownloader.api.DownloadCallBack;
import com.ding.learn.resdownloader.api.DownloadResponse;
import com.ding.learn.resdownloader.api.Downloader;
import com.ding.learn.resdownloader.api.OnDownloaderDestroyListener;
import com.ding.learn.resdownloader.core.DownloadResponseImpl;
import com.ding.learn.resdownloader.core.DownloaderImpl;
import com.ding.learn.resdownloader.db.DataBaseManager;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class DownloadManager {

    private static volatile DownloadManager mInstance;

    private HashMap<DownloadRequest, Downloader> mDownloadMap;
    private ExecutorService mExecutor;

    private DownloadManager() {
        mDownloadMap = new HashMap<>();
        mExecutor = Executors.newFixedThreadPool(5);
    }

    public static DownloadManager getInstance() {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        DataBaseManager.getInstance().init(context);
    }

    private OnDownloaderDestroyListener mDownloaderDestroyListener = new OnDownloaderDestroyListener() {
        @Override
        public void onDestroy(DownloadRequest key, Downloader downloader) {
            if (key != null && mDownloadMap.containsKey(key)) {
                mDownloadMap.remove(key);
            }
        }
    };

    public void download(DownloadRequest request, DownloadCallBack callBack) {
        if (request == null) {
            return;
        }
        Downloader downloader = mDownloadMap.get(request);
        if (downloader != null) {
            if (!downloader.isRuning()) {
                downloader.start();
            }
            return;
        }
        DownloadResponse response = new DownloadResponseImpl(callBack);
        downloader = new DownloaderImpl(request, response, mExecutor, mDownloaderDestroyListener);
        downloader.start();
        mDownloadMap.put(request, downloader);
    }

    public void pause(DownloadRequest request) {
        if (request == null) {
            return;
        }
        Downloader downloader = mDownloadMap.get(request);
        if (downloader != null) {
            downloader.pause();
        }
    }

    public void cancle(DownloadRequest request) {
        if (request == null) {
            return;
        }
        Downloader downloader = mDownloadMap.get(request);
        if (downloader != null) {
            downloader.cancle();
        }
    }
}
