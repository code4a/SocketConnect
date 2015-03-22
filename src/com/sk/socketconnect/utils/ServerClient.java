package com.sk.socketconnect.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerClient {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new FileClient("c:\\e\\test_socket\\1.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/*
 * TCP通讯 需求:传送任意文件
 * 
 * tcp传输要定义2个端点,客户端和服务端 步骤 1.定义服务, 服务器ip和接收端口 2.读取客户端已有的图片文件
 * 3.使用socket输出流将数据发给服务端 4.读取服务端的反馈信息 5.关闭资源
 * 
 * 注意: 1.在while循环中的read方法最后读到文件结束标记-1后循环退出了
 * 而没有将该标记写入socket流中,那么服务端接收到的数据是不完整的,而且停不下来 用shutdownOutput方法告诉服务端文件到了末尾结束发送了
 * 
 * [示例]:传送任意文件 (客户端)
 */

class FileClient // 客户端
{
    FileClient(String fileStr) throws Exception {
        s.op("客户端启动....");
        File file = new File(fileStr); // 关联一个文件c:\\FoxitReader_CHS.rar
        if (file.isFile()) // 是一个标准文件吗?
        {
            client(file); // 启动连接
        } else {
            s.op("要发送的文件 " + fileStr + " 不是一个标准文件,请正确指定");
        }
    }

    public void client(File file) throws Exception {
        Socket sock = new Socket("192.168.1.116", 3999); // 指定服务端地址和端口

        FileInputStream fis = new FileInputStream(file); // 读取本地文件
        OutputStream sockOut = sock.getOutputStream(); // 定义socket输出流

        // 先发送文件名.让服务端知道
        // String fileName = file.getName();
        String fileName = "{_IMAGE_START_}";
        s.op("待发送文件:" + fileName);
        sockOut.write(fileName.getBytes());

        String serverInfo = servInfoBack(sock); // 反馈的信息:服务端是否获取文件名并创建文件成功
        s.op("接收到服务器响应:" + serverInfo);
        if (serverInfo.equals("{_IMAGE_READY_}")) // 服务端说已经准备接收文件,发吧
        {
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
            sockOut.write("{_IMAGE_END_}".getBytes());
        } else {
            s.op("服务端返回信息:" + serverInfo);
        }
        sock.shutdownOutput(); // 必须的,要告诉服务端该文件的数据已写完
        s.op("服务端最后一个返回信息:" + servInfoBack(sock));// 显示服务端最后返回的信息

        fis.close();
        sock.close();
    }

    public String servInfoBack(Socket sock) throws Exception // 读取服务端的反馈信息
    {
        InputStream sockIn = sock.getInputStream(); // 定义socket输入流
//        BufferedReader sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        byte[] bufIn = new byte[1024];
        int lenIn = sockIn.read(bufIn); // 将服务端返回的信息写入bufIn字节缓冲区
        String info = new String(bufIn, 0, lenIn);
        return info;
    }
}

class s {
    public static void op(Object obj) // 打印
    {
        System.out.println(obj);
    }
}
