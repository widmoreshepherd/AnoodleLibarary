package com.cnbot.baiduvoice.speech.wakeup;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.aispeech.AIEchoConfig;
import com.aispeech.AIError;
import com.aispeech.DUILiteConfig;
import com.aispeech.DUILiteSDK;
import com.aispeech.common.AIConstant;
import com.aispeech.export.config.AILocalSignalAndWakeupConfig;
import com.aispeech.export.engines2.AILocalSignalAndWakeupEngine;
import com.aispeech.export.intent.AILocalSignalAndWakeupIntent;
import com.aispeech.export.listeners.AILocalSignalAndWakeupListener;
import com.cnbot.baiduvoice.speech.utils.SampleConstants;

import java.util.HashMap;

/**
 * Copyright (c) 2016-$today.year Hunan CNBOT Co., Ltd. All Rights Reserved.
 *
 * @Description 思必弛语音唤醒
 * @FileName: $file.fileName
 * @author:
 * @date:
 * @version:
 */
public class SpeechWakeUpTool {
    private static final String TAG = SpeechWakeUpTool.class.getSimpleName();
    private Context mContext;
    private static volatile SpeechWakeUpTool mInstance;
    // 唤醒状态
    private boolean isWakeup = false;
    private static boolean haveAuth = false;
    private boolean isInit = false;
    private AILocalSignalAndWakeupEngine mEngine;

    private AILocalSignalAndWakeupIntent intent = new AILocalSignalAndWakeupIntent();

    public void setiSpeechWakeupListener(ISpeechWakeupListener iSpeechWakeupListener) {
        this.iSpeechWakeupListener = iSpeechWakeupListener;
    }

    private ISpeechWakeupListener iSpeechWakeupListener;

    private SpeechWakeUpTool(Context context) {
        mContext = context;
    }

    public static SpeechWakeUpTool getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SpeechWakeUpTool.class) {
                if (mInstance == null) {
                    mInstance = new SpeechWakeUpTool(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 语音唤醒授权
     * @return
     */
   public boolean speechAuth(){
       // 产品认证需设置 apiKey, productId, productKey, productSecret
       DUILiteConfig config = new DUILiteConfig(
               "493b0674ad76493b0674ad765ff530fd",
               "279599377",
               "1e3808c1061badd666132bfea6a31112",
               "6b6492daac34a4020971c0d68259479d");
       config.setAuthTimeout(5000); //设置授权连接超时时长，默认5000ms

       //设置SDK录音模式
//        config.setMaxVolumeMode(true);//开启大音量模式
       config.setAudioRecorderType(DUILiteConfig.TYPE_COMMON_CIRCLE4);//环形四麦模式

       // echo模式 ==>
       // config.setAudioRecorderType(DUILiteConfig.TYPE_COMMON_ECHO);
       if (config.getAudioRecorderType() == DUILiteConfig.TYPE_COMMON_ECHO) {
           AIEchoConfig aiEchoConfig = new AIEchoConfig();
           aiEchoConfig.setAecResource(SampleConstants.ECHO__RES); // 设置echo的AEC资源文件
           aiEchoConfig.setChannels(2); //音频总的通道数
           aiEchoConfig.setMicNumber(1); //真实mic数
           // 默认为1,即左通道为rec录音音频,右通道为play参考音频（播放音频）
           // 若设置为2,通道会互换，即左通道为play参考音频（播放音频）,右通道为rec录音音频
           aiEchoConfig.setRecChannel(1);
           aiEchoConfig.setSavedDirPath("/sdcard/aispeech/aecPcmFile/");//设置保存的aec原始输入和aec之后的音频文件路径

           config.setEchoConfig(aiEchoConfig);
       }
       // echo模式 <==

       config.openLog();//仅输出SDK logcat日志，须在init之前调用.
//        config.openLog("/sdcard/duilite/DUILite_SDK.log");//输出SDK logcat日志，同时保存日志文件在/sdcard/duilite/DUILite_SDK.log，须在init之前调用.


       String core_version = DUILiteSDK.getCoreVersion();//获取内核版本号
       Log.d(TAG, "core version is: " + core_version);

       boolean isAuthorized = DUILiteSDK.isAuthorized(mContext);//查询授权状态，DUILiteSDK.init之后随时可以调
       Log.d(TAG, "DUILite SDK is isAuthorized ？ " + isAuthorized);

       DUILiteSDK.init(mContext,
               config,
               new DUILiteSDK.InitListener() {
                   @Override
                   public void success() {
                       Log.i(TAG, "run: 授权成功");
                       initWakeup();
                       haveAuth = true;
                   }

                   @Override
                   public void error(final String errorCode, final String errorInfo) {
                       Log.i(TAG, "error: 授权失败 nErrorCode：" + errorCode + " +   ErrorInfo：" + errorInfo);
                       haveAuth = false;
                   }
               });
       return haveAuth;
   }

    /**
     * 开启唤醒
     */
   public void startWakeup(){

       // 设置音频保存路径，会保存原始多声道音频(in_时间戳.pcm)和经过beamforming后的单声道音频(out_时间戳.pcm)
       intent.setSaveAudioFilePath("/sdcard/speech");
       mEngine.start(intent);
       setDynamicDoa(70);
       isWakeup = true;
   }

    /**
     * 停止唤醒
     */
    public void stopWakeup() {
        if (mEngine != null) {
            mEngine.stop();
            isWakeup = false;
        }
    }

    /**
     * 销毁
     */
    public void release() {
        if (mEngine != null) {
            stopWakeup();
            mEngine.destroy();
        }
    }

    /**
     * 唤醒状态
     * @return
     */
    public boolean isWakeup() {
        return isWakeup;
    }

    /**
     * 初始化
     */
    public void initWakeup() {
        // 初始化唤醒对象
        AILocalSignalAndWakeupConfig config = new AILocalSignalAndWakeupConfig();
        // 设置aec资源, 设置了资源就启用AEC，没设置就不启用AEC
        // config.setAecResource(SampleConstants.AEC_RES);
        // config.setEchoChannelNum(0);    // 设置参考音路数

        config.setBeamformingResource(SampleConstants.CIRCLE4_BEAMFORMING_RES);//设置环形四麦bf资源


        config.setWakeupResource(SampleConstants.WAKEUP_RES);//设置唤醒资源
        // 设置唤醒词 和 是否是主唤醒词,1表示主唤醒词，0表示副唤醒词
        config.setWakeupWord(new String[]{"ni hao xiao le"}, new int[]{1});
        config.setThreshold(new float[]{0.34f});//设置唤醒词对应的阈值
        config.setLowThreshold(new float[]{0.25f});//设置电视大音量场景下的预唤醒阈值，若非大音量场景下，无需配置
        config.setRollBackTime(1200);//oneshot回退的时间，单位为ms(只有主唤醒词才会回退音频,即major为1)

        // 设置唤醒词的另一种方式，和上面的方法效果一样
        // config.setWakeupword(new WakeupWord("ni hao xiao le", 0.34f, 0.25f, 1, 0));


        mEngine = AILocalSignalAndWakeupEngine.createInstance();
        mEngine.init(config, new AILocalSignalAndWakeupListenerImpl());
    }

    private class AILocalSignalAndWakeupListenerImpl implements AILocalSignalAndWakeupListener {


        @Override
        public void onInit(int status) {
            Log.i(TAG, "Init result " + status);
            if (status == AIConstant.OPT_SUCCESS) {
                Log.i(TAG, "onInit: 初始化成功");
                isInit = true;
                if(iSpeechWakeupListener != null){
                    iSpeechWakeupListener.wakeupInitSuccess();
                }
            } else {
                isInit = false;
                if(iSpeechWakeupListener != null){
                    iSpeechWakeupListener.wakeupInitFial();
                }
                Log.e(TAG, "初始化失败!code:" + status);
            }
        }

        @Override
        public void onError(AIError error) {
            Log.e(TAG, "onError: " + error.toString()  );
            if(iSpeechWakeupListener != null){
                iSpeechWakeupListener.wakeupFialError(error.toString());
            }
        }

        @Override
        public void onWakeup(double confidence, String wakeupWord) {
            Log.d(TAG, "onWakeup: 唤醒成功 confidence=" + confidence + " wakeupWord = " + wakeupWord);
            if(iSpeechWakeupListener != null){
                iSpeechWakeupListener.wakeupSuccess(wakeupWord);
            }
        }

        @Override
        public void onDoaResult(int doa) {
            Log.d(TAG, "唤醒的角度:" + doa);
        }

        @Override
        public void onReadyForSpeech() {
            Log.i(TAG, "你可以用你好小乐来唤醒" + "\n");
        }

        @Override
        public void onRawDataReceived(byte[] buffer, int size) {

        }

        @Override
        public void onResultDataReceived(byte[] buffer, int size, int wakeup_type) {
        }

        @Override
        public void onVprintCutDataReceived(int dataType, byte[] data, int size) {

        }

        @Override
        public void onAgcDataReceived(byte[] bytes, int i) {

        }
    }


    /**
     * 多麦全部支持设置doa，可以影响bf输出的音频
     *
     * @param doa 一开始唤醒增强的角度
     */
    public void setDynamicDoa(final int doa) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mEngine == null)
                    return;
                HashMap<String, Object> map = new HashMap<>();
                map.put("doa", doa);
                mEngine.setDynamicParam(map);
            }
        }, 2000);
    }



}