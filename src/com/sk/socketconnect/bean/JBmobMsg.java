package com.sk.socketconnect.bean;

import com.sk.socketconnect.utils.JBmobConfig;

public class JBmobMsg {

    private String mBelongId;
    private int mMsgType;
    private String mBelongAvatar;
    private String mBelongUsername;
    private String mMsgTime;
    private String mContent;
    private int mStatus;

    public JBmobMsg(String mBelongId, String mMsgTime, String mContent) {
        this(mBelongId, JBmobConfig.TYPE_TEXT, "", "jiang", mMsgTime,
                mContent, 1001);
    }

    public JBmobMsg(String mBelongId, int mMsgType, String mBelongAvatar,
            String mBelongUsername, String mMsgTime, String mContent,
            int mStatus) {
        super();
        this.mBelongId = mBelongId;
        this.mMsgType = mMsgType;
        this.mBelongAvatar = mBelongAvatar;
        this.mBelongUsername = mBelongUsername;
        this.mMsgTime = mMsgTime;
        this.mContent = mContent;
        this.mStatus = mStatus;
    }

    public void setBelongId(String mBelongId) {
        this.mBelongId = mBelongId;
    }

    public void setMsgType(int mMsgType) {
        this.mMsgType = mMsgType;
    }

    public void setBelongAvatar(String mBelongAvatar) {
        this.mBelongAvatar = mBelongAvatar;
    }

    public void setBelongUsername(String mBelongUsername) {
        this.mBelongUsername = mBelongUsername;
    }

    public void setMsgTime(String mMsgTime) {
        this.mMsgTime = mMsgTime;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public String getBelongId() {
        return mBelongId;
    }

    public int getMsgType() {
        return mMsgType;
    }

    public String getBelongAvatar() {
        return mBelongAvatar;
    }

    public String getBelongUsername() {
        return mBelongUsername;
    }

    public String getMsgTime() {
        return mMsgTime;
    }

    public String getContent() {
        return mContent;
    }

    public int getStatus() {
        return mStatus;
    }

}
