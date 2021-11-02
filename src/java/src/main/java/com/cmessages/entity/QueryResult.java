package com.cmessages.entity;

public class QueryResult {
    protected Integer id;
    protected String mobile;
    protected Integer messageStatus;

    public QueryResult(Integer id, String mobile, Integer messageStatus) {
        this.id = id;
        this.mobile = mobile;
        this.messageStatus = messageStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public String toString() {
        String statusStr;
        switch (messageStatus) {
            case 0:
                statusStr = "SUCCESS";
                break;
            case 1:
                statusStr = "PENDING";
                break;
            case 2:
                statusStr = "SENDING";
                break;
            case 3:
                statusStr = "FAILED";
                break;
            default:
                statusStr = "Unknown(" + messageStatus + ")";
                break;
        }
        return "QueryResult{" +
                "mobile='" + mobile + '\'' +
                ", status='" + statusStr +
                "'}";
    }
}
