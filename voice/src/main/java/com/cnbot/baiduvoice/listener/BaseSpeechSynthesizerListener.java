package com.cnbot.baiduvoice.listener;

import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;

public abstract class BaseSpeechSynthesizerListener implements SpeechSynthesizerListener {

    private String tag;
    private String id;

    public BaseSpeechSynthesizerListener(String tag, String id){
        this.tag = tag;
        this.id = id;
    }

    @Override
    public void onSynthesizeStart(String s) {
        Log.e(tag,id + "准备开始合成,序列号:" + s);
        onSynthesizeStart();
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        //Log.e(tag,id + " onSynthesizeDataArrived: s = " +  s + ",i = " + i);
    }

    @Override
    public void onSynthesizeFinish(String s) {
        Log.e(tag,id + "合成结束回调, 序列号:" + s);
        onSynthesizeFinish();
    }

    @Override
    public void onSpeechStart(String s) {
        Log.e(tag,id + "播放开始回调, 序列号:" + s);
        onSpeechStart();
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        onSpeechProgress(s,i);
    }

    @Override
    public void onSpeechFinish(String s) {
        Log.e(tag,id + "播放结束回调, 序列号:" + s);
        onSpeechFinished(s);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        String error = id + "错误发生：" + speechError.description + "，错误编码：" + speechError.code + "，序列号:" + s;
        Log.e(tag,error);
        onError(error,speechError.code);
    }

    public abstract void onSynthesizeStart();

    public abstract void onSynthesizeFinish();

    public abstract void onSpeechStart();

    public abstract void onSpeechProgress(String s,int progress);

    public abstract void onSpeechFinished(String s);

    public abstract void onError(String error,int code);

}
