package com.sk.socketconnect.interf;

public interface OnRequestSockerServerListener {

    /**
     * request failed
     */
    void onRequestFailed();

    /**
     * request success
     * 
     * @param result
     */
    void onRequestSuccess(String result);

    /**
     * request before doing sth
     */
    void onPrepareRequest();
}
