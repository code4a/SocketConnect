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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String getTaskListMsg = getIntent().getStringExtra(Constant.GETTASK_RESULT);
        mTaskList = new ArrayList<String>();
        String[] mTaskArray = getTaskListMsg.split(",");

        for (int i = 1; i < mTaskArray.length; i++) {
            mTaskList.add(mTaskArray[i]);
        }
        mtlaAdapter = new TaskListActAdapter(this, mTaskList);
        mListView.setAdapter(mtlaAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemMsg = (String) parent.getItemAtPosition(position);
                String[] itemMsgArr = itemMsg.split(" ");

                String requestMsg = appendRequest(Constant.GETTASKPOINT, itemMsgArr[1] + "," + itemMsgArr[2]);
                sendRequest(requestMsg, false, null, new OnRequestStateListener() {
                    @Override
                    public void onRequestSuccess(String result) {
                        // TASKPOINTLIST，x y,x y
                        if (result.contains(Constant.GETTASKPOINT_SUCCESS)) {
                            result = result.substring(result.indexOf(",") + 1);
                            Bundle pBundle = new Bundle();
                            pBundle.putString(Constant.GETTASKPOINT_RESULT, result);
                            openActivity(JBaiduMapActivity.class, pBundle);
                        }

                    }

                    @Override
                    public void onRequestFailed() {
                        // TODO Auto-generated method stub

                    }
                });

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

}
