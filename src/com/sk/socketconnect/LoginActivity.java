package com.sk.socketconnect;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sk.socketconnect.base.BaseActivity;
import com.sk.socketconnect.interf.OnRequestStateListener;
import com.sk.socketconnect.utils.Constant;

public class LoginActivity extends BaseActivity {

    private EditText main_act_username, main_act_password;
    private Button main_act_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.login_act_login:
            String username = main_act_username.getText().toString().trim();
            String password = main_act_password.getText().toString().trim();
            if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                String loginMsg = username + "," + password;
                String requestMsg = appendRequest(Constant.LOGIN, loginMsg);
                sendRequestMsg(requestMsg);
            }else{
                showShortToast("请输入用户名或密码！");
            }
            break;

        default:
            break;
        }
    }

    @Override
    protected void mFindViewByIdAndSetListener() {
        main_act_username = $(R.id.login_act_username);
        main_act_password = $(R.id.login_act_password);
        main_act_login = $(R.id.login_act_login);

        main_act_login.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onFailed() {
        showLongToast("request failed");
    }

    @Override
    public void onSuccess(String result) {
        String[] resultArr = result.split(",");
        if(Constant.LOGIN_RESULT_SUCCESS.equals(resultArr[0])){
            Bundle pBundle = new Bundle();
            pBundle.putString(Constant.LOGIN_RESULT, resultArr[1]);
            openActivity(ControlActivity.class, pBundle);
            finish();
        }else{
            showShortToast("登陆失败" + result);
        }
    }

}
