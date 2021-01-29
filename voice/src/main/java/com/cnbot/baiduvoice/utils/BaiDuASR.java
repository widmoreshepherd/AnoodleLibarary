package com.cnbot.baiduvoice.utils;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.cnbot.baiduvoice.listener.IRecogListener;
import com.cnbot.baiduvoice.listener.RecogEventAdapter;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 75213 on 2017/12/19.
 */

public class BaiDuASR{
    private final String TAG = "BaiDuASR";

    private static boolean isInited = false;
    private EventManager asr;
    private Context context;
    private EventListener eventListener = null;



    private static volatile BaiDuASR instanll = null;

    private BaiDuASR(Context context) {
        context = context;
    }
    public static BaiDuASR getInstanll(Context context) {
        if (instanll == null) {
            synchronized (BaiDuASR.class) {
                if (instanll == null) {
                    instanll = new BaiDuASR(context);
                }
            }
        }
        return instanll;
    }

    /**
     * 初始化
     *
     * @param recogListener 将EventListener结果做解析的DEMO回调。使用RecogEventAdapter 适配EventListener
     */

    public void setBaiduAsrListener( IRecogListener recogListener){
        new RecogEventAdapter(recogListener);
        this.eventListener =  new RecogEventAdapter(recogListener);
    }

    /**
     * 初始化
     */
    public void initASR(){
        isInited = true;
        asr = EventManagerFactory.create(context, "asr");
        asr.registerListener(eventListener); //  EventListener 中 onEvent方法
    }

    /**
     * 描述：开始识别
     */
    public void startASR(){
        Log.e(TAG , "开始识别");
        Map<String ,Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.PID , 15361); //普通话
        params.put(SpeechConstant.VAD , SpeechConstant.VAD_DNN);
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT , 2000); //长时间录音
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false); //禁止音量回掉
        String event = SpeechConstant.ASR_START; //识别事件
        String json = new JSONObject(params).toString();
        asr.send(event , json , null ,0 , 0);
    }

    public void start(Map<String, Object> params) {
        String json = new JSONObject(params).toString();
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }
    /**
     * 暂停识别
     */
    public void stopASR(){
        if (asr != null){
            Log.e(TAG , "停止识别");
            asr.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
            asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        }
    }

    /**
     * 取消本次识别，取消后将立即停止不会返回识别结果。
     * cancel 与stop的区别是 cancel在stop的基础上，完全停止整个识别流程，
     */
    public void cancel() {
        Log.d(TAG, "取消识别");
        if (asr != null) {
            asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        }
    }

    /**
     * 提前结束录音等待识别结果。
     */
    public void stop() {
        Log.d(TAG, "停止录音");
        asr.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    /**
     * 描述：释放资源
     */
    public void releaseASR(){
        if (asr == null){
            return;
        }
        stopASR();
        asr.unregisterListener(eventListener);
        asr = null;
        isInited = false;
    }

    /**
     * 解析工具
     */
     public String getText(String str){
        String startStr = "sult\":\"";
        String endStr = "\",\"res";
        int start = str.indexOf(startStr) + startStr.length();
        int end = str.indexOf(endStr);
        Log.e(TAG , "int" + start + end);
        if (start > 0 && end > 0){
            return str.substring(start , end);
        }else {
            return "";
        }
    }


}
