package com.sk.socketconnect.socket;

import android.os.AsyncTask;

import com.sk.socketconnect.interf.OnRequestSockerServerListener;

public class JSocketClientHelper {

    // private Context mContext;
    // private Handler mHander;

    private JSocketClientHelper(/* Context mContext, Handler mHandler */) {
        // this.mContext = mContext;
        // this.mHander = mHandler;
    }

    // private static JSocketClientHelper mInstance = null;
    private static final JSocketClientHelper mInstance = new JSocketClientHelper();

    public static JSocketClientHelper getInstance(/*
                                                   * Context mContext, Handler
                                                   * mHandler
                                                   */) {
        // if (mInstance == null) {
        // mInstance = new JSocketClientHelper(/*mContext, mHandler*/);
        // }
        return mInstance;
    }

    public void requestSocketGetResult(String requestMsg, final OnRequestSockerServerListener orssl) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                orssl.onPrepareRequest();
            }

            @Override
            protected String doInBackground(String... requestMsg) {
                return JSocketClient.getInstance().getServerMsg(requestMsg[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(result == null){
                    orssl.onRequestFailed();
                    return;
                }
                if ("".equals(result) || !(result.startsWith("{") && result.endsWith("}"))) {
                    orssl.onRequestFailed();
                } else {
                    result = result.substring(1, result.length()-1);
                    orssl.onRequestSuccess(result);
                }
            }
        }.execute(new String[] { requestMsg });
    }
}
