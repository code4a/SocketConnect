package com.sk.socketconnect.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.sk.socketconnect.interf.OnRequestSockerServerListener;
import com.sk.socketconnect.interf.OnRequestStateListener;
import com.sk.socketconnect.socket.JSocketClientHelper;

public abstract class BaseActivity extends FragmentActivity implements
        OnClickListener, OnRequestStateListener {

    protected Dialog mDialog;

    // protected boolean isIMTalk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        mFindViewByIdAndSetListener();
    }

    protected abstract void mFindViewByIdAndSetListener();

    protected abstract int getLayoutId();

    protected String appendRequest(String action, String requestMessage/*
                                                                        * ,
                                                                        * String
                                                                        * trim2
                                                                        */) {
        StringBuffer requestMsg = new StringBuffer();
        return requestMsg.append("{").append(action).append(",")
                .append(requestMessage)
                /* .append(",").append(trim2) */.append("}").toString();
    }

    // protected void sendRequest(String requestMsg) {
    // JSocketClientHelper.getInstance().requestSocketGetResult(requestMsg,
    // orssl);
    // }

    protected void sendRequest(Object obj) {
        JSocketClientHelper.getInstance().requestSocketGetResult(obj, orssl);
    }

    private OnRequestSockerServerListener orssl = new OnRequestSockerServerListener() {

        @Override
        public void onRequestFailed() {
            safeDimissDialog();
            onFailed();
        }

        @Override
        public void onRequestSuccess(String result) {
            safeDimissDialog();
            onSuccess(result);
        }

        @Override
        public void onPrepareRequest() {
            mDialog = ProgressDialog.show(BaseActivity.this, "", "please wait");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    protected void safeDimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    protected void showLongToast(int pResId) {
        showLongToast(getStringId(pResId));
    }

    protected void showShortToast(int pResId) {
        showShortToast(getStringId(pResId));
    }

    protected void showLongToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
    }

    protected String getStringId(int resId) {
        return getResources().getString(resId);
    }

    protected boolean hasExtra(String pExtraKey) {
        if (getIntent() != null) {
            return getIntent().hasExtra(pExtraKey);
        }
        return false;
    }

    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    protected <T extends View> T $(int resId) {
        return (T) findViewById(resId);
    }

    protected <T extends View> T $(View v, int resId) {
        return (T) v.findViewById(resId);
    }

    protected void printLog(String log) {
        Log.i(this.getClass().getSimpleName(), log);
    }
}
