package com.ding.learn.resdownloader;

import java.io.File;

/**
 * Created by jindingwei on 2019/7/6.
 */

public class DownloadRequest {
    private String url;
    private String name;
    private File mFolder;
    private String mTag;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public File getFolder() {
        return mFolder;
    }

    public String getTag() {
        return mTag;
    }

    public DownloadRequest(String url, String name, File folder) {
        this.url = url;
        this.name = name;
        this.mFolder = folder;
        this.mTag = url;
    }

    public static class Builder {
        private String mUrl;
        private String mName;
        private File mFolder;

        public Builder() {

        }

        public DownloadRequest build() {
            return new DownloadRequest(mUrl, mName, mFolder);
        }

        public Builder setUrl(String mUrl) {
            this.mUrl = mUrl;
            return this;
        }

        public Builder setName(String mName) {
            this.mName = mName;
            return this;
        }

        public Builder setFolder(File mFolder) {
            this.mFolder = mFolder;
            return this;
        }
    }
}
