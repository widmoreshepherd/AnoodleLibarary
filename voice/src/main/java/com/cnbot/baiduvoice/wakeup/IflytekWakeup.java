package com.cnbot.baiduvoice.wakeup;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.cnbot.baiduvoice.wakeup.listener.IflytekWakeupListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONObject;

public class IflytekWakeup {

    private static final String TAG = "wakeup";

    public static void init(Context context){
        StringBuffer param = new StringBuffer();
        param.append("appid=5ffd3e83");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(context, param.toString());
    }

    private Context context;
    private VoiceWakeuper mIvw;

    // 设置门限值 ： 门限值越低越容易被唤醒
    private final static int MAX = 3000;
    private final static int MIN = 0;
    private int curThresh = 1200;
    private String keep_alive = "1";
    private String ivwNetMode = "0";

    private static volatile IflytekWakeup instance = null;

    private IflytekWakeupListener iflytekWakeupListener = null;

    private IflytekWakeup(Context context,int curThresh){
        this.context = context;
        this.curThresh = curThresh;
        mIvw = VoiceWakeuper.createWakeuper(context, null);
    }

    public static IflytekWakeup getInstance(Context context,int curThresh){
        if(instance==null){
            synchronized (IflytekWakeup.class){
                if(instance==null){
                    instance = new IflytekWakeup(context, curThresh);
                }
            }
        }
        return instance;
    }

    public void setIflytekWakeupListener(IflytekWakeupListener iflytekWakeupListener) {
        this.iflytekWakeupListener = iflytekWakeupListener;
    }

    public void initWakeUp(){
        //非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        Log.e(TAG,mIvw==null?"mIvw=null":"mIvw!=null");
        if(mIvw != null&&!mIvw.isListening()) {
            // 清空参数
            mIvw.setParameter(com.iflytek.cloud.SpeechConstant.PARAMS, null);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(com.iflytek.cloud.SpeechConstant.IVW_THRESHOLD, "0:"+ curThresh);
            // 设置唤醒模式
            mIvw.setParameter(com.iflytek.cloud.SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(com.iflytek.cloud.SpeechConstant.KEEP_ALIVE, keep_alive);
            // 设置闭环优化网络模式
            mIvw.setParameter(com.iflytek.cloud.SpeechConstant.IVW_NET_MODE, ivwNetMode);
            // 设置唤醒资源路径
            mIvw.setParameter(com.iflytek.cloud.SpeechConstant.IVW_RES_PATH, getResource());
            // 设置唤醒录音保存路径，保存最近一分钟的音频
            //mIvw.setParameter( com.iflytek.cloud.SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath()+"/msc/ivw.wav" );
            mIvw.setParameter( com.iflytek.cloud.SpeechConstant.AUDIO_FORMAT, "wav" );
            // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
            //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
            // 启动唤醒
            /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/
            mIvw.startListening(mWakeuperListener);
        } else {
            Log.e(TAG,"唤醒未初始化");
        }
    }

    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "ivw/5ffd3e83.jet");
        return resPath;
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {
        @Override
        public void onResult(WakeuperResult result) {
            try {
                String text = result.getResultString();
                Log.e(TAG,text);
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                String score = object.optString("score");
                buffer.append("【RAW】 "+text);
                buffer.append("\n");
                buffer.append("【操作类型】"+ object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】"+ object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + score);
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                Log.e(TAG,buffer.toString());
                if(Integer.valueOf(score)>=curThresh){
                    mIvw.stopListening();
                    if(iflytekWakeupListener!=null){
                        iflytekWakeupListener.wakeupSuccess();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG,"结果解析出错");
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError error) {
            Log.e(TAG,"mWakeuperListener onError:" + error.getErrorCode() + ":" + error.toString());
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            Log.e( TAG, "mWakeuperListener onEvent : eventType = " + eventType);
            switch( eventType ){
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray( SpeechEvent.KEY_EVENT_RECORD_DATA );
                    Log.e( TAG, "ivw audio length: "+audio.length );
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };

    public void stopListening(){
        if(mIvw!=null){
            Log.e(TAG,"停止唤醒监听");
            mIvw.stopListening();
        }
    }

    public void destory(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
    }


}
