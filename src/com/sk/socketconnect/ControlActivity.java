package com.sk.socketconnect;

import android.os.Bundle;
import android.view.View;

import com.sk.socketconnect.base.BaseActivity;
import com.sk.socketconnect.interf.OnRequestStateListener;
import com.sk.socketconnect.utils.Constant;

public class ControlActivity extends BaseActivity {

    private String mBundleResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundleResult = getIntent().getStringExtra(Constant.LOGIN_RESULT);
        printLog("mBundleResult = " + mBundleResult);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.control_act_get_task:
            String requestMsg = appendRequest(Constant.GETTASK, mBundleResult);
            sendRequest(requestMsg, false, null, new OnRequestStateListener() {
                
                @Override
                public void onRequestSuccess(String result) {
                    if(result.contains(Constant.GETTASK_RESULT_SUCCESS)){
                        Bundle pBundle = new Bundle();
                        pBundle.putString(Constant.GETTASK_RESULT, result);
                        openActivity(TaskListActivity.class, pBundle);
                    }
                }
                
                @Override
                public void onRequestFailed() {
                    showLongToast("request failed !!! ");
                }
            });
            break;
        case R.id.control_act_get_img:
            openActivity(UnLoadImageDetial.class);
            break;
        case R.id.control_act_chat:
            
            break;

        default:
            break;
        }
    }

    @Override
    protected void mFindViewByIdAndSetListener() {
        $(R.id.control_act_get_task).setOnClickListener(this);
        $(R.id.control_act_get_img).setOnClickListener(this);
        $(R.id.control_act_chat).setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_control;
    }
    
    

}
