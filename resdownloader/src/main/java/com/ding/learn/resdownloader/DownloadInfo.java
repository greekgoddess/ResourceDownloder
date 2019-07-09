package com.ding.learn.resdownloader;

import java.io.File;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class DownloadInfo {
    private String mUrl;
    private String mName;
    private File mDir;
    private long mFinishedLength;
    private long mTotalLength;

    public DownloadInfo(String name, String url, File dir) {
        mName = name;
        mUrl = url;
        mDir = dir;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public File getDir() {
        return mDir;
    }

    public void setDir(File dir) {
        this.mDir = dir;
    }

    public long getFinishedLength() {
        return mFinishedLength;
    }

    public void setFinishedLength(long finishedLength) {
        this.mFinishedLength = finishedLength;
    }

    public long getTotalLength() {
        return mTotalLength;
    }

    public void setTotalLength(long totalLength) {
        this.mTotalLength = totalLength;
    }
}
