package com.sk.socketconnect.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sk.socketconnect.R;

public class TaskListActAdapter extends BaseAdapter {

    private List<String> mTaskList;
    private Context mContext;

    public TaskListActAdapter(Context mContext, List<String> mTaskList) {
        this.mContext = mContext;
        this.mTaskList = mTaskList;
    }

    @Override
    public int getCount() {
        return mTaskList == null ? 0 : mTaskList.size();
    }

    @Override
    public String getItem(int position) {
        return mTaskList == null ? null : mTaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if(convertView == null){
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.task_list_act_task_list_item, null);
            mViewHolder.mTaskMsg = $(convertView, R.id.task_list_act_task_list_item_tv);
            convertView.setTag(mViewHolder);
        }
        mViewHolder = (ViewHolder) convertView.getTag();
        mViewHolder.mTaskMsg.setText(getItem(position));
        return convertView;
    }
    
    class ViewHolder{
        TextView mTaskMsg;
    }
    
    protected <T extends View> T $(View mView, int resId) {
        return (T) mView.findViewById(resId);
    }

}
