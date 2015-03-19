package com.sk.socketconnect.socket;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.os.SystemClock;
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

    public String getServerMsg(String requestMsg, boolean isExtraStream, InputStream mis) {
        String responseMsg = "";
        boolean isSuccess = false;
        try {
            Socket socket = new Socket(Constant.HOST, Constant.PORT);
            
            OutputStream mos = socket.getOutputStream();
            
            OutputStreamWriter os = new OutputStreamWriter(mos, Constant.ENCODING);
            // 由Socket对象得到输出流，并构造PrintWriter对象
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            if(isExtraStream && mis != null){
                isSuccess = sendIOStream(mis, mos);
            }else{
                if(requestMsg != null){
                    os.write(requestMsg);
                    os.flush();
                    isSuccess = false;
                }else{
                    return "";
                }
            }


            if(isSuccess){
                SystemClock.sleep(1000);
                responseMsg = "{_SEND_IMAGE_SUCCESS_}";
            }else{
                responseMsg = is.readLine();
            }
            os.close(); // 关闭Socket输出流
            is.close(); // 关闭Socket输入流
            socket.close();// 关闭Socket
        } catch (Exception e) {
            Log.i(TAG,"log is : " + e.getMessage());
            System.out.println("Error" + e); // 出错，则打印出错信息
        }
        return responseMsg;
    }

    private boolean sendIOStream(InputStream mIS, OutputStream mOS) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(mOS);
            byte[] buf = new byte[1024 * 1024 * 10];
            int len = 0 ;
            while((len=mIS.read(buf))!=-1){
                bos.write(buf,0,len);
                bos.flush();
                Log.i(this.getClass().getSimpleName(), " ---- write ----- " + len);
                //printLog("");
            }
        } catch (Exception e) {
            Log.i(TAG,"log is : " + e.getMessage());
            System.out.println("Error" + e);
            return false;
        }finally{
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
}
