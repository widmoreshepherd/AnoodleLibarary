package com.anoodle.webapi.socket;

import android.os.Handler;
import android.os.Message;

import com.anoodle.webapi.utils.LogUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread{

    private static final String TAG = "Server";
    private static final String CHARSET = "UTF-8";

    private ServerSocket serverSocket;
    private Socket socket;
    private static final int PORT = 12349;

    private Handler handler;

    private static final String END = "command\r\n";
    private byte[] endBytes;

    public ServerThread(Handler handler){
        this.handler=handler;
        endBytes = END.getBytes();
    }

    public static final int SERVER_CONNECT = 0;
    public static final int SERVER_MESSAGE = 1;
    public static final int SERVER_DISCONNECT = 2;
    private volatile byte[] bytes = new byte[0];

    public boolean isOpen(){
        if(serverSocket==null){
            return false;
        }
        return !serverSocket.isClosed();
    }

    @Override
    public void run() {
        try {
            if(serverSocket!=null){
                serverSocket.close();
            }
            if (socket!=null) {
                socket.close();
            }
            sleep(3000);
            if(serverSocket!=null){
                serverSocket.setReuseAddress(true);
            }
            serverSocket = new ServerSocket(PORT);
            LogUtils.e(TAG,"已开启");
            socket = serverSocket.accept();
            LogUtils.e(TAG,"设备已连接");
            handler.sendEmptyMessage(SERVER_CONNECT);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            byte buffer[] = new byte[1024];
            int len;
            while((len=inputStream.read(buffer))!=-1){
                byte[] data = new byte[len];
                for(int i=0;i<data.length;i++) {
                    data[i] = buffer[i];
                }
                bytes = mergeByte(bytes,data);
                if(bytes.length<endBytes.length){
                    continue;
                }
                String dataString = new String(bytes,CHARSET);
                if(!dataString.contains(END)){
                    continue;
                }
                int index = dataString.indexOf(END);
                String msg = dataString.substring(0,index);
                int length = dataString.length();
                dataString = dataString.substring(index,length);
                dataString = dataString.replaceFirst(END, "");
                bytes = dataString.getBytes();
                LogUtils.e(TAG,"收到消息:" + msg);
                Message message = Message.obtain();
                message.what = SERVER_MESSAGE;
                message.obj = msg;
                handler.sendMessage(message);
            }
            socket.close();
            serverSocket.close();
            LogUtils.e(TAG,"关闭了");
            handler.sendEmptyMessage(SERVER_DISCONNECT);
            LogUtils.e(TAG,"重启了");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] mergeByte(byte[] a, byte[] b) {
        byte[] add = new byte[a.length + b.length];
        int i;
        for (i = 0; i < a.length; i++) {
            add[i] = a[i];
        }
        for (int k = 0; k<b.length; k++, i++) {
            add[i] = b[k];
        }
        return add;
    }

    public void close(){
        LogUtils.e(TAG,"close");
        try {
            Thread.sleep(3000);
            if (socket!=null) {
                socket.close();
            }
            if (serverSocket!=null) {
                serverSocket.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
