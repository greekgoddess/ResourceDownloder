package com.ding.learn.resdownloader.core;

import com.ding.learn.resdownloader.DownloadStatus;
import com.ding.learn.resdownloader.DownloadStatusDelivery;
import com.ding.learn.resdownloader.api.DownloadCallBack;
import com.ding.learn.resdownloader.api.DownloadResponse;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class DownloadResponseImpl implements DownloadResponse {

    private DownloadStatus mStatus;
    private DownloadStatusDelivery mStatusDelivery;

    public DownloadResponseImpl(DownloadCallBack callBack) {
        mStatus = new DownloadStatus();
        mStatus.setCallBack(callBack);
        mStatusDelivery = new DownloadStatusDelivery();
    }

    @Override
    public void onStarted() {
        mStatus.status = DownloadStatus.DOWNLOAD_START;
        if (mStatus.getCallBack() != null) {
            mStatus.getCallBack().onStarted();
        }
    }

    @Override
    public void onConnecting() {
        mStatus.status = DownloadStatus.DOWNLOAD_CONNECTING;
        mStatusDelivery.post(mStatus);
    }

    @Override
    public void onConnected(long length, boolean isAllowRang) {
        mStatus.status = DownloadStatus.DOWNLOAD_CONNECTED;
        mStatus.totalLength = length;
        mStatus.isAllowRang = isAllowRang;
        mStatusDelivery.post(mStatus);
    }

    @Override
    public void onProgressUpdate(long finished, long total, float percent) {
        mStatus.status = DownloadStatus.DOWNLOAD_PROGRESS;
        mStatus.finishedLength = finished;
        mStatus.totalLength = total;
        mStatus.percent = percent;
        mStatusDelivery.post(mStatus);
    }

    @Override
    public void onCompleted() {
        mStatusDelivery.removeUnHandlerRunnable();
        mStatus.status = DownloadStatus.DOWNLOAD_COMPLETED;
        mStatusDelivery.post(mStatus);
    }

    @Override
    public void onPaused() {
        mStatus.status = DownloadStatus.DOWNLOAD_PAUSED;
        mStatusDelivery.post(mStatus);
    }

    @Override
    public void onCancled() {
        mStatus.status = DownloadStatus.DOWNLOAD_CANCLED;
        mStatusDelivery.post(mStatus);
    }

    @Override
    public void onFailed() {
        mStatus.status = DownloadStatus.DOWNLOAD_FAILED;
        mStatusDelivery.post(mStatus);
    }
}
