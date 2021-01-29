package com.cnbot.baiduvoice.listener;

import android.util.Log;

import com.baidu.speech.asr.SpeechConstant;
import com.cnbot.baiduvoice.asr.listener.IRecogListener;
import com.cnbot.baiduvoice.asr.util.RecogResult;

public abstract class BaseRecogListener implements IRecogListener {

    private String tag;
    private String id;

    public BaseRecogListener(String tag,String id){
        this.tag = tag;
        this.id = id;
    }

    @Override
    public void onAsrReady() {
        Log.e(tag,id + "引擎就绪，可以开始说话");
        onVoiceAsrReady();
    }

    @Override
    public void onAsrBegin() {
        Log.e(tag,id + "检测到用户说话");
        onVoiceAsrBegin();
    }

    @Override
    public void onAsrEnd() {
        Log.e(tag,id + "【asr.end事件】检测到用户说话结束");
        onVoiceAsrEnd();
    }

    @Override
    public void onAsrPartialResult(String[] results, RecogResult recogResult) {
        //Log.e(tag,id +  SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL + "临时识别结果，结果是“" + results[0] + "”；原始json：" + recogResult.getOrigalJson());
    }

    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        String message = id + "识别结束，结果是”" + results[0] + "”";
        Log.e(tag, SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL + ":" + message + "；原始json：" + recogResult.getOrigalJson());
        String name = results[0];
        onAsrFinalResult(name);
    }

    @Override
    public void onAsrFinish(RecogResult recogResult) {

    }

    @Override
    public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage, RecogResult recogResult) {
        String message = id + "【asr.finish事件】识别错误, 错误码：" + errorCode + " ," + subErrorCode + " ; " + descMessage;
        Log.e(tag,message);
        onAsrFinishError(message);
    }

    @Override
    public void onAsrLongFinish() {

    }

    @Override
    public void onAsrExit() {
        Log.e(tag ,id + "识别引擎结束并空闲中");
    }


    public abstract void onVoiceAsrReady();

    public abstract void onVoiceAsrBegin();

    public abstract void onVoiceAsrEnd();

    public abstract void onAsrFinalResult(String result);

    public abstract void onAsrFinishError(String message);

}
