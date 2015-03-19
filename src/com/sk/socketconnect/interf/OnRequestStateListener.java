package com.sk.socketconnect.interf;

public interface OnRequestStateListener {

    /**
     * request failed
     */
    void onFailed();

    /**
     * request success
     * @param result
     */
    void onSuccess(String result);
}
