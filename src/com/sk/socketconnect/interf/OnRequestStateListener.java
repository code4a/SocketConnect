package com.sk.socketconnect.interf;

public interface OnRequestStateListener {

    /**
     * request failed
     */
    void onRequestFailed();

    /**
     * request success
     * @param result
     */
    void onRequestSuccess(String result);
}
