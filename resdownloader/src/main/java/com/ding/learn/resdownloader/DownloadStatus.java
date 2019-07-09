package com.ding.learn.resdownloader;

import com.ding.learn.resdownloader.api.DownloadCallBack;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class DownloadStatus {
    public static final int DOWNLOAD_START = 101;

    public static final int DOWNLOAD_CONNECTING = 102;

    public static final int DOWNLOAD_CONNECTED = 103;

    public static final int DOWNLOAD_PAUSED = 104;

    public static final int DOWNLOAD_CANCLED = 105;

    public static final int DOWNLOAD_PROGRESS = 106;

    public static final int DOWNLOAD_COMPLETED = 107;

    public static final int DOWNLOAD_FAILED = 108;

    public int status;
    public long finishedLength;
    public long totalLength;
    public boolean isAllowRang;
    public float percent;
    private DownloadCallBack mCallBack;

    public DownloadStatus() {

    }

    public DownloadCallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(DownloadCallBack callBack) {
        this.mCallBack = callBack;
    }
}
