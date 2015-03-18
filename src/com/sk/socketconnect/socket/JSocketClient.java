package com.sk.socketconnect.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    public String getServerMsg(String requestMsg) {
        String responseMsg = "";
        try {
            Socket socket = new Socket(Constant.HOST, Constant.PORT);
            // 由系统标准输入设备构造BufferedReader对象
            OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream(), Constant.ENCODING);
            // 由Socket对象得到输出流，并构造PrintWriter对象
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os.write(requestMsg);

            os.flush();

            responseMsg = is.readLine();

            os.close(); // 关闭Socket输出流
            is.close(); // 关闭Socket输入流
            socket.close();// 关闭Socket
        } catch (Exception e) {
            Log.i(TAG,"log is : " + e.getMessage());
            System.out.println("Error" + e); // 出错，则打印出错信息
        }
        return responseMsg;
    }
}
