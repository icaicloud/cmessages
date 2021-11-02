package com.cmessages.entity;

public class SendResult {
    protected Integer id; // 发送结果的短信ID
    protected String mobile; // 发送的短信

    public SendResult(Integer id, String mobile) {
        this.id = id;
        this.mobile = mobile;
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

    @Override
    public String toString() {
        return "SendResult{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
