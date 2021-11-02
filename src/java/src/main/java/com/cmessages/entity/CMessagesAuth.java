package com.cmessages.entity;

public class CMessagesAuth {
    protected String appId;
    protected String appKey;

    public CMessagesAuth(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Override
    public String toString() {
        return "CMessagesAuth{" +
                "appId='" + appId + '\'' +
                ", appKey='" + "***" + '\'' +
                '}';
    }
}

