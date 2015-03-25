package com.sk.socketconnect.socket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.sk.socketconnect.interf.OnIMTalkMsgTypeListener;
import com.sk.socketconnect.interf.OnReceiveMessageStateListener;
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

    // private List<String> mMessageQueue = new ArrayList<String>();
    private BlockingQueue<String> mMessageQueue = new LinkedBlockingQueue<String>();

    public void sendMessage(final String sendMsg) {
        printLog("发送消息 ===== > " + sendMsg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printLog("消息存入消息队列");
                    mMessageQueue.put(sendMsg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (ormsl != null) {
                        ormsl.onReceiveMsgFaile();
                    }
                }
            }
        }).start();
    }

    public void requestSocketGetResult(String type, Object requestMsg, /*
                                                           * final boolean
                                                           * isExtraStream,
                                                           * final InputStream
                                                           * mis,
                                                           */
            final OnRequestSockerServerListener orssl) {
        new AsyncTask<Object, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                orssl.onPrepareRequest();
            }

            @Override
            protected String doInBackground(Object... requestMsg) {
                return JSocketClient.getInstance()
                        .getServerMsg((String)requestMsg[0], requestMsg[1]/* , isExtraStream, mis */);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result == null) {
                    orssl.onRequestFailed();
                    return;
                }
                if (result.startsWith("{") && result.endsWith("}")) {
                    result = result.substring(1, result.length() - 1);
                    orssl.onRequestSuccess(result);
                }else{
                    orssl.onRequestFailed();
                }
            }
        }.execute(new Object[] {type, requestMsg });
    }

    private OnReceiveMessageStateListener ormsl;

    public void onOpenTalkIM(OnReceiveMessageStateListener ormsl) {
        this.ormsl = ormsl;
        onStartTalk();
        printLog("异步启动聊天socket");
    }

    public void onCloseTalkIM() {
        JSocketClient.getInstance().onIMTalkClose();
    }

    // private int talkingstate = -1;
    private OnIMTalkMsgTypeListener moimtmtl = new OnIMTalkMsgTypeListener() {

        @Override
        public String onIMSendMsg() {
            // printLog("-----------------------------获取消息！！！！！！----------------------------");
            return mMessageQueue.poll();
        }

        @Override
        public void onIMReceiveMsg(Object obj) {
            if (ormsl != null) {
                ormsl.onReceiveMsgSuccess((String) obj);
            }
        }
    };

    private void onStartTalk() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isReady = JSocketClient.getInstance().isReady();
                printLog("聊天 socket 创建成功：" + isReady);
                if (isReady) {
                    JSocketClient.getInstance().onTalkSendMsg(moimtmtl);
                    printLog(" 调用 onTalking ");
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                printLog("启动接受消息的方法");
                JSocketClient.getInstance().onTalkReceiveMsg(moimtmtl);
            }
        }).start();
    }

    protected void printLog(String log) {
        Log.i(this.getClass().getSimpleName(), log);
    }
}
