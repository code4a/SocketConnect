package com.sk.socketconnect;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sk.socketconnect.adapter.MessageChatAdapter;
import com.sk.socketconnect.base.BaseActivity;
import com.sk.socketconnect.bean.JBmobMsg;
import com.sk.socketconnect.interf.OnReceiveMessageStateListener;
import com.sk.socketconnect.socket.JSocketClientHelper;
import com.sk.socketconnect.utils.CommonUtils;
import com.sk.socketconnect.utils.Constant;
import com.sk.socketconnect.view.EmoticonsEditText;
import com.sk.socketconnect.view.HeaderLayout;
import com.sk.socketconnect.view.HeaderLayout.HeaderStyle;
import com.sk.socketconnect.view.HeaderLayout.onLeftImageButtonClickListener;
import com.sk.socketconnect.view.HeaderLayout.onRightImageButtonClickListener;
import com.sk.socketconnect.view.xlist.XListView;
import com.sk.socketconnect.view.xlist.XListView.IXListViewListener;

public class IMChatActivity extends BaseActivity implements IXListViewListener {

    // BmobUserManager userManager;
    // BmobChatManager manager;
    protected HeaderLayout mHeaderLayout;

    protected int mScreenWidth;
    protected int mScreenHeight;

    private Button btn_chat_emo, btn_chat_send, btn_chat_add, btn_chat_keyboard, btn_speak, btn_chat_voice;

    XListView mListView;

    EmoticonsEditText edit_user_comment;

    String targetId = "1001";

    // BmobChatUser targetUser;

    // private static int MsgPagerNum;

    private LinearLayout layout_more, layout_emo, layout_add;

    // private ViewPager pager_emo;

    // private TextView tv_picture, tv_camera, tv_location;

    // 语音有关
    RelativeLayout layout_record;
    TextView tv_voice_tips;
    ImageView iv_record;

    // private Drawable[] drawable_Anims;// 话筒动画

    // BmobRecordManager recordManager;

    private MessageChatAdapter mAdapter;

    private OnReceiveMessageStateListener ormsl = new OnReceiveMessageStateListener() {

        @Override
        public void onReceiveMsgSuccess(String receiveMsg) {
            printLog("接收到消息 ======= > " + receiveMsg);
            Message msg = mHandler.obtainMessage();
            receiveMsg = receiveMsg.substring(receiveMsg.lastIndexOf(",") + 1, receiveMsg.indexOf("}"));
            msg.obj = receiveMsg;
            msg.what = RECEIVE_SERVER_MSG;
            mHandler.sendMessage(msg);
            // JBmobMsg mjbm = new JBmobMsg(targetId, receiveMsg);
            // refreshMessage(mjbm);
        }

        @Override
        public void onReceiveMsgFaile() {
            showShortToast("发送失败！");
        }
    };

    private String user_id;

    private static final int RECEIVE_SERVER_MSG = 0x11;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Object receiveMsg = msg.obj;
            switch (msg.what) {
            case RECEIVE_SERVER_MSG:
                String currentTime = System.currentTimeMillis() + "";
                JBmobMsg mjbm = new JBmobMsg(targetId, currentTime, (String) receiveMsg);
                refreshMessage(mjbm);
                break;

            default:
                break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        user_id = getIntent().getStringExtra(Constant.USER_ID);
        printLog(" ------- onCreate -------  初始化聊天socket");
        JSocketClientHelper.getInstance().onOpenTalkIM(ormsl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JSocketClientHelper.getInstance().onCloseTalkIM();
        printLog("关闭 soket");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.edit_user_comment:// 点击文本输入框
            mListView.setSelection(mListView.getCount() - 1);
            if (layout_more.getVisibility() == View.VISIBLE) {
                layout_add.setVisibility(View.GONE);
                layout_emo.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
            }
            break;
        case R.id.btn_chat_emo:// 点击笑脸图标
            // if (layout_more.getVisibility() == View.GONE) {
            // showEditState(true);
            // } else {
            // if (layout_add.getVisibility() == View.VISIBLE) {
            // layout_add.setVisibility(View.GONE);
            // layout_emo.setVisibility(View.VISIBLE);
            // } else {
            // layout_more.setVisibility(View.GONE);
            // }
            // }

            break;
        case R.id.btn_chat_add:// 添加按钮-显示图片、拍照、位置
            // if (layout_more.getVisibility() == View.GONE) {
            // layout_more.setVisibility(View.VISIBLE);
            // layout_add.setVisibility(View.VISIBLE);
            // layout_emo.setVisibility(View.GONE);
            // hideSoftInputView();
            // } else {
            // if (layout_emo.getVisibility() == View.VISIBLE) {
            // layout_emo.setVisibility(View.GONE);
            // layout_add.setVisibility(View.VISIBLE);
            // } else {
            // layout_more.setVisibility(View.GONE);
            // }
            // }

            break;
        case R.id.btn_chat_voice:// 语音按钮
            // edit_user_comment.setVisibility(View.GONE);
            // layout_more.setVisibility(View.GONE);
            // btn_chat_voice.setVisibility(View.GONE);
            // btn_chat_keyboard.setVisibility(View.VISIBLE);
            // btn_speak.setVisibility(View.VISIBLE);
            // hideSoftInputView();
            break;
        case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
            showEditState(false);
            break;
        case R.id.btn_chat_send:// 发送文本
            final String msg = edit_user_comment.getText().toString();
            if (msg.equals("")) {
                showShortToast("请输入发送消息!");
                return;
            }
            boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
            if (!isNetConnected) {
                showShortToast(R.string.network_tips);
                // return;
            }
            // 组装BmobMessage对象
            // BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
            // // 默认发送完成，将数据保存到本地消息表和最近会话表中
            // manager.sendTextMessage(targetUser, message);
            String currentTime = System.currentTimeMillis() + "";
            JBmobMsg message = new JBmobMsg("1000", currentTime, msg);
            // 默认发送完成，将数据保存到本地消息表和最近会话表中
            String sendMsg = appendRequest(Constant.IMTALK, "2," + user_id + "," + msg);
            // 刷新界面
            refreshMessage(message);
            sendTalkMessage(sendMsg);

            break;
        case R.id.tv_camera:// 拍照
            // selectImageFromCamera();
            break;
        case R.id.tv_picture:// 图片
            // selectImageFromLocal();
            break;
        case R.id.tv_location:// 位置
            // selectLocationFromMap();
            break;
        default:
            break;
        }
    }

    private void sendTalkMessage(String sendMsg) {
        // onSuccess(sendMsg);
        // sendRequest(sendMsg);
        JSocketClientHelper.getInstance().sendMessage(sendMsg);
    }

    /**
     * 刷新界面
     * 
     * @Title: refreshMessage
     * @Description:
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshMessage(JBmobMsg msg) {
        // 更新界面
        mAdapter.add(msg);
        mListView.setSelection(mAdapter.getCount() - 1);
        edit_user_comment.setText("");
    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     * 
     * @Title: showEditState
     * @Description:
     * @param @param isEmo: 用于区分文字和表情
     * @return void
     * @throws
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    // 显示软键盘
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(edit_user_comment, 0);
        }
    }

    @Override
    protected void mFindViewByIdAndSetListener() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        // $(R.id.control_act_get_task).setOnClickListener(this);
        // $(R.id.control_act_get_img).setOnClickListener(this);
        // $(R.id.control_act_chat).setOnClickListener(this);

        // BmobLog.i("聊天对象：" + targetUser.getUsername() + ",targetId = "
        // + targetId);
        // 注册广播接收器
        // initNewMessageBroadCast();
        initView();
    }

    private void initView() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mListView = (XListView) findViewById(R.id.mListView);
        initTopBarForLeft("与" + "蒋" + "对话");
        initBottomView();
        initXListView();
        // initVoiceView();
    }

    private void initBottomView() {
        // 最左边
        btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
        btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
        btn_chat_add.setOnClickListener(this);
        btn_chat_emo.setOnClickListener(this);
        // 最右边
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);
        // 最下面
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);
        // initAddView();
        // initEmoView();

        // 最中间
        // 语音框
        btn_speak = (Button) findViewById(R.id.btn_speak);
        // 输入框
        edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void initXListView() {
        // 首先不允许加载更多
        mListView.setPullLoadEnable(false);
        // 允许下拉
        mListView.setPullRefreshEnable(true);
        // 设置监听器
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mListView.setDividerHeight(0);
        // 加载数据
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                hideSoftInputView();
                layout_more.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_send.setVisibility(View.GONE);
                return false;
            }
        });

        // 重发按钮的点击事件
        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend, new MessageChatAdapter.onInternalClickListener() {

            @Override
            public void OnClickListener(View parentV, View v, Integer position, Object values) {
                // 重发消息
                // showResendDialog(parentV, v, values);
            }
        });
    }

    private void initOrRefresh() {
        if (mAdapter != null) {
            // if (MyMessageReceiver.mNewNum != 0) {//
            // ���ڸ��µ���������������ڼ�������Ϣ����ʱ�ٻص�����ҳ���ʱ����Ҫ��ʾ��������Ϣ
            // int news=
            // MyMessageReceiver.mNewNum;//�п��������ڼ䣬����N����Ϣ,�����Ҫ������ʾ�ڽ�����
            // int size = initMsgData().size();
            // for(int i=(news-1);i>=0;i--){
            // mAdapter.add(initMsgData().get(size-(i+1)));//
            // ������һ����Ϣ��������ʾ
            // }
            // mListView.setSelection(mAdapter.getCount() - 1);
            // } else {
            // mAdapter.notifyDataSetChanged();
            // }
        } else {
            mAdapter = new MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(mAdapter);
        }
    }

    private List<JBmobMsg> initMsgData() {
        List<JBmobMsg> mList = new ArrayList<JBmobMsg>();
        String currentTime = System.currentTimeMillis() + "";
        mList.add(new JBmobMsg(targetId, currentTime, "hello world"));
        return mList;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_im_chat;
    }

    @Override
    public void onFailed() {
        showLongToast("request failed !!! ");
    }

    @Override
    public void onSuccess(String result) {
        String currentTime = System.currentTimeMillis() + "";
        JBmobMsg mjbm = new JBmobMsg(targetId, currentTime, result);
        refreshMessage(mjbm);
    }

    /**
     * 隐藏软键盘 hideSoftInputView
     * 
     * @Title: hideSoftInputView
     * @Description:
     * @param
     * @return void
     * @throws
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 只有title initTopBarLayoutByTitle
     * 
     * @Title: initTopBarLayoutByTitle
     * @throws
     */
    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    /**
     * 初始化标题栏-带左右按钮
     * 
     * @return void
     * @throws
     */
    public void initTopBarForBoth(String titleName, int rightDrawableId, String text, onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName, R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text, listener);
    }

    public void initTopBarForBoth(String titleName, int rightDrawableId, onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName, R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId, listener);
    }

    // 左边按钮的点击事件
    public class OnLeftButtonClickListener implements onLeftImageButtonClickListener {

        @Override
        public void onClick() {
            finish();
        }
    }

    /**
     * 只有左边按钮和Title initTopBarLayout
     * 
     * @throws
     */
    public void initTopBarForLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName, R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
    }

}
