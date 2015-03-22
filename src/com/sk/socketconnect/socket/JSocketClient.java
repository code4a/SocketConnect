package com.sk.socketconnect.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

import com.sk.socketconnect.interf.OnIMTalkMsgTypeListener;
import com.sk.socketconnect.utils.Constant;

public class JSocketClient {

    public static final JSocketClient mInstance = new JSocketClient();
    private static final String TAG = "JSocketClient";

    private Socket mIMScoket;
    private static boolean isSocketOpen = false;
    private boolean isTalking = false;

    private JSocketClient() {
    }

    public static JSocketClient getInstance() {
        return mInstance;
    }

    // public String getServerMsg(String requestMsg) {
    // return onSocketClient(requestMsg);
    // }
    public String getServerMsg(Object obj) {
        if (obj == null) {
            return "";
        }
        return onSocketClient(obj);
    }

    private String servInfoBack(Socket sock) {
        InputStream sockIn;
        byte[] bufIn = new byte[1024];
        int lenIn = 0;
        try {
            sockIn = sock.getInputStream();
            lenIn = sockIn.read(bufIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(lenIn < 0){
            return "";
        }
        String info = new String(bufIn, 0, lenIn);
        return info;
    }

    private boolean onIMTalkOpen() {
        try {
            mIMScoket = new Socket(Constant.HOST, Constant.PORT);
            printLog("创建聊天 socket");
            if (mIMScoket != null) {
                isSocketOpen = true;
                printLog("创建聊天 socket 成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSocketOpen;
    }

    public void onIMTalkClose() {
        if (isSocketOpen) {
            try {
                isTalking = false;
                mIMScoket.close();
                printLog("成功关闭聊天 socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isReady() {
        isTalking = true;
        return onIMTalkOpen();
    }

    public void onTalkSendMsg(OnIMTalkMsgTypeListener moimtmtl) {
        // int ret = -1;
        try {
            OutputStream sockOut = mIMScoket.getOutputStream();

//            InputStream sockIn = mIMScoket.getInputStream();
//            byte[] bufIn = new byte[1024];
//            int lenIn = 0;
            if (moimtmtl == null) {
                return;
            }
            String sendMsg = null;
//            String receiveMsg = null;
            printLog("获取io流，发送消息");
            while (isTalking) {
                sendMsg = moimtmtl.onIMSendMsg();
                //printLog("检测消息");
                if (sendMsg != null /*&& !"".equals(sendMsg)*/) {
                    printLog("获取到发送端消息，发送消息  =====>" + sendMsg);
                    sockOut.write(sendMsg.getBytes());
                }
//                lenIn = sockIn.read(bufIn);
//                receiveMsg = new String(bufIn, 0, lenIn);
//                if (receiveMsg != null && !"".equals(receiveMsg)) {
//                    printLog("接收到服务端消息");
//                    moimtmtl.onIMReceiveMsg(receiveMsg);
//                }
                // ret = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return ret;
    }
    public void onTalkReceiveMsg(OnIMTalkMsgTypeListener moimtmtl) {
        try {
            InputStream sockIn = mIMScoket.getInputStream();
            byte[] bufIn = new byte[1024];
            int lenIn = 0;
            if (moimtmtl == null) {
                return;
            }
            String receiveMsg = null;
            while (isTalking) {
                printLog("等待接收服务端消息");
                lenIn = sockIn.read(bufIn);
                receiveMsg = new String(bufIn, 0, lenIn);
                if (receiveMsg != null && !"".equals(receiveMsg)) {
                    printLog("接收到服务端消息 ===========> " + receiveMsg);
                    moimtmtl.onIMReceiveMsg(receiveMsg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String onSocketClient(Object obj) {
        String serverInfo = "";
        try {
            Socket sock = new Socket(Constant.HOST, Constant.PORT);
            sock.setSoTimeout(8*1000);
            OutputStream sockOut = sock.getOutputStream();
            if (obj instanceof String) {
                sockOut.write(((String) obj).getBytes());
                // serverInfo = servInfoBack(sock);
                // return servInfoBack(sock);
            }
            // serverInfo = servInfoBack(sock);
            if (obj instanceof File) {
                boolean isSucress = sendFileIOStream(((File) obj), sock);
                if (isSucress) {
                    sockOut.write("{_IMAGE_END_}".getBytes());
                }/*else{
                    sockOut.write("{_IMAGE_END_FAIL_}".getBytes());
                }*/
            }
            serverInfo = servInfoBack(sock);
            sock.shutdownOutput();
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serverInfo;
    }

    private boolean sendFileIOStream(File mFile, Socket sock) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(mFile);
            OutputStream sockOut = sock.getOutputStream();

            // String fileName = mFile.getName();
            String fileName = "{_IMAGE_START_}";
            sockOut.write(fileName.getBytes());

            String serverInfo = servInfoBack(sock);
            if (serverInfo.equals("{_IMAGE_READY_}")) {
                byte[] bufFile = new byte[1024];
                int len = 0;
//                while (true) {
//                    len = fis.read(bufFile);
//                    if (len != -1) {
//                        sockOut.write(bufFile, 0, len); // 将从硬盘上读取的字节数据写入socket输出流
//                    } else {
//                        break;
//                    }
//                }
                while ((len = fis.read(bufFile))!=-1) {
                    sockOut.write(bufFile, 0, len); // 将从硬盘上读取的字节数据写入socket输出流
                }
                //Thread.sleep(2000);
            }
            //sockOut.write("{_IMAGE_END_}".getBytes());
        } catch (Exception e) {
            Log.i(TAG, "log is : " + e.getMessage());
            System.out.println("Error" + e);
            return false;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    protected void printLog(String log) {
        Log.i(this.getClass().getSimpleName(), log);
    }
}
