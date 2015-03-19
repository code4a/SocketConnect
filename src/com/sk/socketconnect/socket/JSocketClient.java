package com.sk.socketconnect.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

import com.sk.socketconnect.utils.Constant;

public class JSocketClient {

    public static final JSocketClient mInstance = new JSocketClient();
    private static final String TAG = "JSocketClient";

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
        String info = new String(bufIn, 0, lenIn);
        return info;
    }

    private String onSocketClient(Object obj) {
        String serverInfo = "";
        try {
            Socket sock = new Socket(Constant.HOST, Constant.PORT);
            OutputStream sockOut = sock.getOutputStream();
            if (obj instanceof String) {
                sockOut.write(((String) obj).getBytes());
                //serverInfo = servInfoBack(sock);
                //return servInfoBack(sock);
            }
            if (obj instanceof File) {
                boolean isSucress = sendFileIOStream(((File) obj), sock);
                if (isSucress) {
                    sockOut.write("{_IMAGE_END_}".getBytes());
                }
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
            byte[] bufFile = new byte[1024];
            int len = 0;
            while (true) {
                len = fis.read(bufFile);
                if (len != -1) {
                    sockOut.write(bufFile, 0, len); // 将从硬盘上读取的字节数据写入socket输出流
                } else {
                    break;
                }
            }
            Thread.sleep(2000);
            // sockOut.write("{_IMAGE_END_}".getBytes());
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
}
