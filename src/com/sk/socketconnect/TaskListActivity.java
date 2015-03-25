package com.sk.socketconnect;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sk.socketconnect.adapter.TaskListActAdapter;
import com.sk.socketconnect.base.BaseActivity;
import com.sk.socketconnect.interf.OnRequestStateListener;
import com.sk.socketconnect.utils.Constant;

public class TaskListActivity extends BaseActivity {

    private ListView mListView;
    private TaskListActAdapter mtlaAdapter;
    private List<String> mTaskList;
    private String currentTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String getTaskListMsg = getIntent().getStringExtra(
                Constant.GETTASK_RESULT);
        mTaskList = new ArrayList<String>();
        String[] mTaskArray = getTaskListMsg.split(",");

        for (int i = 1; i < mTaskArray.length; i++) {
            mTaskList.add(mTaskArray[i]);
        }
        mtlaAdapter = new TaskListActAdapter(this, mTaskList);
        mListView.setAdapter(mtlaAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String itemMsg = (String) parent.getItemAtPosition(position);
                String[] itemMsgArr = itemMsg.split(" ");
                currentTaskId = itemMsgArr[1];
                String requestMsg = appendRequest(Constant.GETTASKPOINT,
                        currentTaskId + "," + itemMsgArr[2]);
                sendRequestMsg(requestMsg);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void mFindViewByIdAndSetListener() {
        mListView = $(R.id.task_list_act_task_list);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_task_list;
    }

    @Override
    public void onFailed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess(String result) {
        // TASKPOINTLIST，x y,x y
        if (result.contains(Constant.GETTASKPOINT_SUCCESS)) {
            result = result.substring(result.indexOf(",") + 1);
            Bundle pBundle = new Bundle();
            pBundle.putString(Constant.GETTASKPOINT_RESULT, result);
            pBundle.putString(Constant.TASK_ID, currentTaskId);
            openActivity(JBaiduMapActivity.class, pBundle);
        }
    }

}
