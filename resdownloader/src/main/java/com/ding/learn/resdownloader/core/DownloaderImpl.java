package com.ding.learn.resdownloader.core;

import android.util.Log;

import com.ding.learn.resdownloader.Constants;
import com.ding.learn.resdownloader.DownloadConfig;
import com.ding.learn.resdownloader.DownloadInfo;
import com.ding.learn.resdownloader.DownloadRequest;
import com.ding.learn.resdownloader.DownloadStatus;
import com.ding.learn.resdownloader.FileUtil;
import com.ding.learn.resdownloader.SubDownloadInfo;
import com.ding.learn.resdownloader.api.ConnectTask;
import com.ding.learn.resdownloader.api.DownloadResponse;
import com.ding.learn.resdownloader.api.DownloadTask;
import com.ding.learn.resdownloader.api.Downloader;
import com.ding.learn.resdownloader.api.OnDownloaderDestroyListener;
import com.ding.learn.resdownloader.db.DataBaseManager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class DownloaderImpl implements Downloader {
    private ExecutorService mExecutor;
    private DownloadRequest mRequest;
    private ConnectTask mConnectTask;
    private DownloadResponse mResponse;
    private LinkedList<DownloadTask> mTaskList;
    private DownloadInfo mDownloadInfo;
    private int mStatus;
    private int mCommand;
    private OnDownloaderDestroyListener mDestoryListener;

    public DownloaderImpl(DownloadRequest request, DownloadResponse response,
                          ExecutorService executorService, OnDownloaderDestroyListener listener) {
        mRequest = request;
        mExecutor = executorService;
        mResponse = response;
        mDestoryListener = listener;

        mTaskList = new LinkedList<>();
        mDownloadInfo = new DownloadInfo(mRequest.getName(), mRequest.getUrl(), mRequest.getFolder());
    }

    @Override
    public boolean isRuning() {
        return mStatus != DownloadStatus.DOWNLOAD_COMPLETED
                && mStatus != DownloadStatus.DOWNLOAD_CANCLED
                && mStatus != DownloadStatus.DOWNLOAD_FAILED
                && mStatus != DownloadStatus.DOWNLOAD_PAUSED;
    }

    @Override
    public boolean isPaused() {
        return mStatus == DownloadStatus.DOWNLOAD_PAUSED;
    }

    @Override
    public void start() {
        mStatus = DownloadStatus.DOWNLOAD_START;
        mResponse.onStarted();
        mConnectTask = new ConnectTaskImpl(mRequest, mConnectListener);
        mExecutor.execute(mConnectTask);
    }

    private ConnectTask.OnConnectListener mConnectListener = new ConnectTask.OnConnectListener() {
        @Override
        public void onConnecting() {
            mStatus = DownloadStatus.DOWNLOAD_CONNECTING;
            mResponse.onConnecting();
        }

        @Override
        public void onConnected(long contentLength, boolean isAllowRang) {
            mStatus = DownloadStatus.DOWNLOAD_CONNECTED;
            mResponse.onConnected(contentLength, isAllowRang);
            mDownloadInfo.setTotalLength(contentLength);
            download(contentLength, isAllowRang);
        }

        @Override
        public void onPaused() {
            Log.e(Constants.DING, "onPaused");
        }

        @Override
        public void onCancled() {
            Log.e(Constants.DING, "onCancled");
        }

        @Override
        public void onConnectFailed() {
            Log.e(Constants.DING, "onConnectFailed");
            mDestoryListener.onDestroy(mRequest, DownloaderImpl.this);
        }
    };

    private boolean isAllCompleted() {
        for (DownloadTask task : mTaskList) {
            if (!task.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllPaused() {
        for (DownloadTask task : mTaskList) {
            if (task.isRuning()) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllCancled() {
        for (DownloadTask task : mTaskList) {
            if (task.isRuning()) {
                return false;
            }
        }
        return true;
    }

    private void download(long contentLength, boolean isAllowRang) {
        mTaskList.clear();
        if (isAllowRang) {
            List<SubDownloadInfo> infoList = DataBaseManager.getInstance().getDownloadInfo(mRequest.getTag());
            if (infoList != null && infoList.size() > 0) {
                int finished = 0;
                for (SubDownloadInfo subDownloadInfo : infoList) {
                    finished += subDownloadInfo.getFinished();
                }
                mDownloadInfo.setFinishedLength(finished);
                for (SubDownloadInfo subDownloadInfo : infoList) {
                    if (subDownloadInfo.getStart() + subDownloadInfo.getFinished() < subDownloadInfo.getEnd()) {
                        DownloadTask task = new MultiThreadDownloadTask(mRequest, mDownloadInfo, subDownloadInfo, mDownloadListener);
                        mTaskList.add(task);
                    }
                }
            } else {
                int subLen = (int) (contentLength / DownloadConfig.sSubTaskNum);
                for (int i = 0; i < DownloadConfig.sSubTaskNum; i++) {
                    long start = 0;
                    long end = 0;
                    start = i * subLen;
                    if (i == DownloadConfig.sSubTaskNum - 1) {
                        end = contentLength;
                    } else {
                        end = start + subLen - 1;
                    }
                    SubDownloadInfo subDownloadInfo = new SubDownloadInfo(start, end, 0);
                    subDownloadInfo.tag = mRequest.getTag();
                    subDownloadInfo.setTaskId(i);
                    DownloadTask multiTask = new MultiThreadDownloadTask(mRequest, mDownloadInfo, subDownloadInfo, mDownloadListener);
                    mTaskList.add(multiTask);
                }
            }
        } else {
            SubDownloadInfo subDownloadInfo = new SubDownloadInfo(0, contentLength, 0);
            subDownloadInfo.tag = mRequest.getTag();
            DownloadTask singleTask = new SingleDownloadTask(mRequest, mDownloadInfo, subDownloadInfo, mDownloadListener);
            mTaskList.add(singleTask);
        }

        if (mTaskList.size() > 0) {
            mStatus = DownloadStatus.DOWNLOAD_PROGRESS;
            for (DownloadTask task : mTaskList) {
                mExecutor.execute(task);
            }
        } else {
            mStatus = DownloadStatus.DOWNLOAD_FAILED;
            mResponse.onFailed();
        }
    }

    private DownloadTask.OnDownloadListener mDownloadListener = new DownloadTask.OnDownloadListener() {
        @Override
        public void onProgressUpdate(long finished, long total, float percent) {
            if (mStatus == DownloadStatus.DOWNLOAD_PROGRESS && mCommand != DownloadStatus.DOWNLOAD_PAUSED) {
                mResponse.onProgressUpdate(finished, total, percent);
            }
        }

        @Override
        public void onCompleted() {
            if (isAllCompleted()) {
                mStatus = DownloadStatus.DOWNLOAD_COMPLETED;
                mResponse.onCompleted();
                DataBaseManager.getInstance().delete(mRequest.getTag());
                mDestoryListener.onDestroy(mRequest, DownloaderImpl.this);
            }
        }

        @Override
        public void onPaused() {
            if (mStatus != DownloadStatus.DOWNLOAD_FAILED) {
                if (isAllPaused()) {
                    Log.e(Constants.DING, "mDownloadListener---onPaused");
                    mStatus = DownloadStatus.DOWNLOAD_PAUSED;
                    mCommand = 0;
                    mResponse.onPaused();
//                    mDestoryListener.onDestroy(mRequest, DownloaderImpl.this);
                }
            }
        }

        @Override
        public void onCancled() {
            if (isAllCancled()) {
                mStatus = DownloadStatus.DOWNLOAD_CANCLED;
                mResponse.onCancled();
                FileUtil.deleteFile(mRequest.getFolder().getPath() + File.separator + mRequest.getName());
                mDestoryListener.onDestroy(mRequest, DownloaderImpl.this);
                DataBaseManager.getInstance().delete(mRequest.getTag());
                Log.e(Constants.DING, "mDownloadListener---onCancled");
            }
        }

        @Override
        public void onFailed() {
            if (mStatus != DownloadStatus.DOWNLOAD_FAILED) {
                mStatus = DownloadStatus.DOWNLOAD_FAILED;
                mResponse.onFailed();
                mDestoryListener.onDestroy(mRequest, DownloaderImpl.this);
                pause();
            }
        }
    };

    @Override
    public void pause() {
        if (isRuning()) {
            mCommand = DownloadStatus.DOWNLOAD_PAUSED;
            for (DownloadTask task : mTaskList) {
                if (task.isRuning()) {
                    task.pause();
                }
            }
        }
    }

    @Override
    public void cancle() {
        if (mStatus != DownloadStatus.DOWNLOAD_PROGRESS) {
            mDownloadListener.onCancled();
        } else {
            for (DownloadTask task : mTaskList) {
                task.cancle();
            }
        }
    }
}
