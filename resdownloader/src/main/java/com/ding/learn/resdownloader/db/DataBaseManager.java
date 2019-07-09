package com.ding.learn.resdownloader.db;

import android.content.Context;


import com.ding.learn.resdownloader.SubDownloadInfo;

import java.util.List;

/**
 * Created by jindingwei on 2019/7/7.
 */

public class DataBaseManager {
    private volatile static DataBaseManager mInstance;
    private SubDownloadInfoDao mDownloadInfoDao;

    private DataBaseManager() {

    }

    public static DataBaseManager getInstance() {
        if (mInstance == null) {
            synchronized (DataBaseManager.class) {
                if (mInstance == null) {
                    mInstance = new DataBaseManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        mDownloadInfoDao = new SubDownloadInfoDao(context);
    }

    public synchronized void insert(SubDownloadInfo downloadInfo) {
        if (mDownloadInfoDao != null) {
            mDownloadInfoDao.insert(downloadInfo);
        }
    }

    public synchronized void updateFinished(String tag, int taskId, long finished) {
        if (mDownloadInfoDao != null) {
            mDownloadInfoDao.updateFinished(tag, taskId, finished);
        }
    }

    public boolean exist(String tag, int taskId) {
        if (mDownloadInfoDao != null) {
            return mDownloadInfoDao.exists(tag, taskId);
        }
        return false;
    }

    public synchronized void delete(String tag) {
        if (mDownloadInfoDao != null) {
            mDownloadInfoDao.delete(tag);
        }
    }

    public synchronized List<SubDownloadInfo> getDownloadInfo(String tag) {
        if (mDownloadInfoDao != null) {
            return mDownloadInfoDao.getSubDownloadInfos(tag);
        }
        return null;
    }
}
