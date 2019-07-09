package com.ding.learn.resdownloader;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class DownloadStatusDelivery {
    private Handler mMianHandler;

    public DownloadStatusDelivery() {
        mMianHandler = new Handler(Looper.getMainLooper());
    }

    public void post(DownloadStatus status) {
        mMianHandler.post(new DownloadStatusDeliveryRunnable(status));
    }

    public void removeUnHandlerRunnable() {
        mMianHandler.removeCallbacksAndMessages(null);
    }

    private static class DownloadStatusDeliveryRunnable implements Runnable {
        private DownloadStatus mStatus;

        public DownloadStatusDeliveryRunnable(DownloadStatus status) {
            mStatus = status;
        }

        @Override
        public void run() {
            switch (mStatus.status) {
                case DownloadStatus.DOWNLOAD_START:
                    if (mStatus.getCallBack() != null) {
                        mStatus.getCallBack().onStarted();
                    }
                    break;
                case DownloadStatus.DOWNLOAD_CONNECTING:
                    if (mStatus.getCallBack() != null) {
                        mStatus.getCallBack().onConnecting();
                    }
                    break;
                case DownloadStatus.DOWNLOAD_CONNECTED:
                    if (mStatus.getCallBack() != null) {
                        mStatus.getCallBack().onConnected(mStatus.totalLength, mStatus.isAllowRang);
                    }
                    break;
                case DownloadStatus.DOWNLOAD_PROGRESS:
                    if (mStatus.getCallBack() != null) {
                        mStatus.getCallBack().onProgressUpdate(mStatus.finishedLength, mStatus.totalLength, mStatus.percent);
                    }
                    break;
                case DownloadStatus.DOWNLOAD_COMPLETED:
                    mStatus.finishedLength = mStatus.totalLength;
                    if (mStatus.getCallBack() != null) {
                        mStatus.getCallBack().onProgressUpdate(mStatus.finishedLength, mStatus.totalLength, mStatus.percent);
                        mStatus.getCallBack().onCompleted();
                    }
                    break;
                case DownloadStatus.DOWNLOAD_FAILED:
                    if (mStatus.getCallBack() != null) {
                        mStatus.getCallBack().onFailed();
                    }
                    break;
                case DownloadStatus.DOWNLOAD_CANCLED:
                    if (mStatus.getCallBack() != null) {
                        mStatus.getCallBack().onCancled();
                    }
                    break;
                case DownloadStatus.DOWNLOAD_PAUSED:
                    if (mStatus.getCallBack() != null) {
                        mStatus.getCallBack().onPaused();
                    }
                    break;

            }
        }
    }
}
