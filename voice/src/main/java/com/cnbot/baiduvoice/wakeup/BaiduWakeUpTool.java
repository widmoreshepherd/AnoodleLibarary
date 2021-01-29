package com.cnbot.baiduvoice.wakeup;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.asr.SpeechConstant;
import com.cnbot.baiduvoice.utils.MyLogger;
import com.cnbot.baiduvoice.wakeup.listener.IBaiduWakeupListener;
import com.cnbot.baiduvoice.wakeup.listener.IWakeupListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2016-$today.year Hunan CNBOT Co., Ltd. All Rights Reserved.
 *
 * @Description 百度语音唤醒
 * @FileName: $file.fileName
 * @author:
 * @date:
 * @version:
 */
public class BaiduWakeUpTool {
    private static final String TAG = BaiduWakeUpTool.class.getSimpleName();

    protected MyWakeup myWakeup;
    private IBaiduWakeupListener iWakeupListener;
    private Context mContext;
    private static volatile BaiduWakeUpTool mInstance;
    // 唤醒状态
    private boolean isWakeup = false;

    private BaiduWakeUpTool(Context context) {
        mContext = context;
    }

    public static BaiduWakeUpTool getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BaiduWakeUpTool.class) {
                if (mInstance == null) {
                    mInstance = new BaiduWakeUpTool(context);
                }
            }
        }
        return mInstance;
    }

    public void initWakeup() {
        // 初始化唤醒对象
        SimpleWakeupListener listener = new SimpleWakeupListener();
        myWakeup = new MyWakeup(mContext, listener);
    }


    // 点击“开始识别”按钮
    // 基于DEMO唤醒词集成第2.1, 2.2 发送开始事件开始唤醒
    public void startWakeup(String appid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", appid);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///xiaonuoxiaonuoxiaodetongxue.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
//        params.put("apikey", "q2uPyBe6LmWTZlvb0g1dzcHV");
        // params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        // params里 "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        if(myWakeup != null) {
            isWakeup = true;
            myWakeup.start(params);
        }
    }


    // 基于DEMO唤醒词集成第4.1 发送停止事件
    public void stopWakeup() {
        if(myWakeup == null){
            return;
        }
        isWakeup = false;
        myWakeup.stop();
    }

    public void release() {
        Log.d(TAG, "onDestroy WakeDemo");
        // 销毁合成对象
        if(myWakeup == null){
            return;
        }
        myWakeup.release();
    }

    public void setmWakeuperListener(IBaiduWakeupListener listener) {
        iWakeupListener = listener;
    }

    public boolean isWakeup() {
        return isWakeup;
    }

    public class SimpleWakeupListener implements IWakeupListener {

        private static final String TAG = "SimpleWakeupListener";

        @Override
        public void onSuccess(String word, WakeUpResult result) {
            MyLogger.info(TAG, "唤醒成功，唤醒词：" + word);
            if(iWakeupListener != null){
                iWakeupListener.wakeupSuccess(word);
            }
        }

        @Override
        public void onStop() {
            MyLogger.info(TAG, "唤醒词识别结束：");
        }

        @Override
        public void onError(int errorCode, String errorMessge, WakeUpResult result) {
            MyLogger.info(TAG, "唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.getOrigalJson());
            if(iWakeupListener != null){
                iWakeupListener.wakeupFialError(errorMessge);
            }
        }

        @Override
        public void onASrAudio(byte[] data, int offset, int length) {
            MyLogger.error(TAG, "audio data： " + data.length);
        }

    }

}
