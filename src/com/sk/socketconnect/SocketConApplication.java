package com.sk.socketconnect;

import android.app.Application;

public class SocketConApplication extends Application {

    private static SocketConApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
    
    public static SocketConApplication getInstance() {
        return mInstance;
    }
}
