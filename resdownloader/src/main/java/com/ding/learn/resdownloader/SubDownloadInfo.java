package com.ding.learn.resdownloader;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class SubDownloadInfo {
    private long start;
    private long end;
    private long finished;

    public String tag;
    private int taskId;

    public SubDownloadInfo() {

    }

    public SubDownloadInfo(long start, long end, long finished) {
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
