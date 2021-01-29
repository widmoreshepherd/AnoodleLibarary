package com.anoodle.webapi.controller;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;

import com.anoodle.webapi.Api;
import com.anoodle.webapi.bean.CnAnswer;
import com.anoodle.webapi.bean.WasteCategory;
import com.anoodle.webapi.http.NetworkManager;
import com.anoodle.webapi.http.ResponseResult;
import com.anoodle.webapi.listener.CnAnswerListener;
import com.anoodle.webapi.listener.DeviceControlListener;
import com.anoodle.webapi.subscribers.ProgressSubscriber;
import com.anoodle.webapi.subscribers.SubscriberOnErrorListener;
import com.anoodle.webapi.subscribers.SubscriberOnNextListener;
import com.anoodle.webapi.utils.DateUtil;
import com.anoodle.webapi.utils.LogUtils;
import com.cnbot.baiduvoice.asr.BaiduRecUtil;
import com.cnbot.baiduvoice.asr.listener.IRecogListener;
import com.cnbot.baiduvoice.listener.BaseRecogListener;
import com.cnbot.baiduvoice.listener.BaseSpeechSynthesizerListener;
import com.cnbot.baiduvoice.utils.BaiduTTS;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CnQAController {

    private static final String TAG = "nnnnnnnnn";

    private Context context;

    private static volatile CnQAController instance = null;

    private Handler handler;
    private Gson gson;
    private boolean isFirstNotUnderStand = true;
    private boolean isVolumeNotUnderStand = true;
    private StringBuffer voiceLogStringBuffer;

    private BaiduRecUtil baiduRecUtil = null;
    private BaiduTTS baiduTTS = null;

    private final Map<String, String> map = new HashMap<>();
    private final String[] volumes = {"六", "七", "八", "九", "十"};
    private String sessionId = "";
    private String tag = "";

    private DeviceControlListener deviceControlListener;
    private CnAnswerListener cnAnswerListener;

    public static final String[] wakeStr = {"在呢~","怎么了","咋了？","我在！","找我？","什么事？","来啦~"};
    private final String[] waitStr = {"小德要学习去啦,再见","小德要休息啦,再见","小德要去做游戏啦,再见","不和小德说话，小德要走咯"};
    private final String[] noReusltStr = {"程序员哥哥没有教我这些","你的问题有点难","现在我还不知道,不过我会努力学习的"};
    public static final String[] confirmStr = {"好的","没问题","稍等哦","马上"};
    public static final String[] answerRightStr = {"选对了，你真棒！","哇！对了哦，真厉害！","就是这个！小天才！"};
    public static final String[] answerWrongStr = {"好可惜，选错了","不对哦，还要努力呀","不对哦","选错了,太可惜了"};

    private static final String VOICE_THANKS = "感谢您对垃圾分类事业的支持";

    public static CnQAController getInstance(Context context){
        if(instance==null){
            synchronized (CnQAController.class){
                if(instance==null){
                    instance = new CnQAController(context);
                }
            }
        }
        return instance;
    }

    private CnQAController(Context context){
        this.context = context;
        handler = new Handler();
        gson = new Gson();
        voiceLogStringBuffer = new StringBuffer();
        map.put("0","可回收垃圾");
        map.put("1","有害垃圾");
        map.put("2","厨余垃圾");
        map.put("3","其他垃圾");
        initBaiduTts();
    }

    private void initBaiduTts(){
        baiduTTS = BaiduTTS.getInstanll();
        baiduTTS.init(context);
    }

    private void initBaduAsrUtil(Context context, IRecogListener listener) {
        baiduRecUtil = new BaiduRecUtil(context, listener);
        baiduRecUtil.initASR();
    }

    private void init(){
        isFirstNotUnderStand = true;
        isVolumeNotUnderStand = true;
        voiceLogStringBuffer = new StringBuffer();
    }

    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }

    public void setDeviceControlListener(DeviceControlListener deviceControlListener) {
        this.deviceControlListener = deviceControlListener;
    }

    public void setCnAnswerListener(CnAnswerListener cnAnswerListener) {
        this.cnAnswerListener = cnAnswerListener;
    }

    public void releaseTTS(){
        if(baiduTTS!=null){
            baiduTTS.release();
        }
    }

    public void cancelASR(){
        if(baiduRecUtil!=null){
            baiduRecUtil.cancelASR();
        }
    }

    public void welcome(String text){
        speekText(sessiongSpeechSynthesizerListener,text);
    }

    public void getAnswer(String id, String query){
        init();
        if(isDeviceControl(query)){
            deviceControl(query);
            return;
        }
        Map<String,String> map = new HashMap<>();
        this.sessionId = id;
        map.put("session_id",id);
        map.put("query",query);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),gson.toJson(map));
        SubscriberOnNextListener<CnAnswer> subscriberOnNextListener = new SubscriberOnNextListener<CnAnswer>() {
            @Override
            public void onNext(CnAnswer cnAnswer) {
                boolean isTrashClassify = false;
                if(cnAnswer!=null){
                    if(cnAnswer.getQuestion_type().equals("Garbage")&&cnAnswer.getIntention().equals("SEARCH_GARBAGE_CLASSIFICATION")){
                        List<Map<String,String>> entities = cnAnswer.getEntities();
                        try {
                            for(int i=0;i<entities.size();i++){
                                Map<String,String> kv = entities.get(i);
                                String name = kv.get("user_garbage");
                                if(name!=null){
                                    isTrashClassify = true;
                                    classifyType(name);
                                }
                            }
                        }catch (Exception e){
                            isTrashClassify = false;
                            e.printStackTrace();
                        }
                        if(!isTrashClassify){
                            int index = new Random().nextInt(noReusltStr.length);
                            String text = noReusltStr[index];
                            speekText(sessiongSpeechSynthesizerListener,text);
                            if(cnAnswerListener!=null){
                                cnAnswerListener.onAnswer(text);
                            }
                        }
                    }else{
                        speekText(sessiongSpeechSynthesizerListener,cnAnswer.getAnswer());
                        if(cnAnswerListener!=null){
                            cnAnswerListener.onAnswer(cnAnswer.getAnswer());
                        }
                    }
                }
            }
        };
        SubscriberOnErrorListener subscriberOnErrorListener = new SubscriberOnErrorListener() {
            @Override
            public void OnError(Throwable e) {
                LogUtils.e(e.toString());
                if(cnAnswerListener!=null){
                    LogUtils.e(TAG,"onEnd():对话查询error");
                    cnAnswerListener.onEnd();
                }
                end();
            }
        };
        ProgressSubscriber<CnAnswer> progressSubscriber = new ProgressSubscriber<>(context,
                subscriberOnNextListener,subscriberOnErrorListener,false,"");
        NetworkManager.getInstance().cnbotGarbage(progressSubscriber,requestBody);
    }

    private boolean isDeviceControl(String query){
        boolean flag = query.equals("设备信息")||query.equals("设备状况") ||
                (query.contains("打开热点") || query.contains("重设wifi"))||
                query.equals("厨余垃圾")||
                query.equals("可回收垃圾")||
                query.equals("有害垃圾")||
                query.equals("其他垃圾")||
                (query.contains("控制音量")|| query.contains("音量控制") || query.contains("设置音量") || query.contains("音量"))||
                query.contains("版本")||
                query.contains("设备序列号")||
                query.contains("满溢状况")||query.contains("满意状况")||
                query.contains("重启");
        return flag;
    }

    private void deviceControl(String query){
        if(deviceControlListener==null){
            return;
        }
        if(query.equals("设备信息")||query.equals("设备状况")){
            deviceControlListener.onGetDeviceInfo();
        }else if(query.contains("打开热点") || query.contains("重设wifi")){
            deviceControlListener.onWifi();
        }else if(query.equals("厨余垃圾")){
            deviceControlListener.onOpenDoor("1");
        }else if(query.equals("可回收垃圾")){
            deviceControlListener.onOpenDoor("2");
        }else if(query.equals("有害垃圾")){
            deviceControlListener.onOpenDoor("3");
        }else if(query.equals("其他垃圾")){
            deviceControlListener.onOpenDoor("4");
        }else if (query.contains("控制音量") || query.contains("音量控制") || query.contains("设置音量") || query.contains("音量")) {
            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            double step = (double) maxVolume / 10;
            int volume = (int) (currentVolume / step);
            LogUtils.e(TAG, "step = " + step + ",currentVolume = " + currentVolume + ",volume = " + volume);
            speekText(volumeSpeechSynthesizerListener, "当前音量为" + Math.round(currentVolume / step) + ",设置范围是6到10,直接说数字即可设置音量");
        } else if (query.contains("版本")) {
            deviceControlListener.onVersion();
        }else if(query.contains("设备序列号")){
            deviceControlListener.onDeviceSN();
        }else if(query.contains("满溢状况")||query.contains("满意状况")) {
            deviceControlListener.onTrashState();
        }else if(query.contains("重启")){
            speekText(new BaseSpeechSynthesizerListener("","") {
                @Override
                public void onSynthesizeStart() {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append( "准备开始合成,序列号\n");
                    }
                }

                @Override
                public void onSynthesizeFinish() {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append( "合成结束回调, 序列号\n");
                    }
                }

                @Override
                public void onSpeechStart() {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append( "播放开始回调, 序列号\n");
                    }
                }

                @Override
                public void onSpeechProgress(String s, int progress) {

                }

                @Override
                public void onSpeechFinished(String s) {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append( "播放结束回调, 序列号\n");
                    }
                    if(deviceControlListener!=null){
                        deviceControlListener.onRestart();
                    }
                }

                @Override
                public void onError(String error, int code) {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append( error + "\n");
                    }
                }
            },"好的");
        }
    }

    private void classifyType(final String name){
        SubscriberOnNextListener<ResponseResult<List<WasteCategory>>> subscriberOnNextListener = new SubscriberOnNextListener<ResponseResult<List<WasteCategory>>>() {
            @Override
            public void onNext(ResponseResult<List<WasteCategory>> listResponseResult) {
                if(listResponseResult.getRetCode()== Api.CODE_SUCCESS){
                    int index = new Random().nextInt(noReusltStr.length);
                    List<WasteCategory> list = listResponseResult.getData();
                    if(list==null||list.size()==0){
                        speekText(sessiongSpeechSynthesizerListener,noReusltStr[index]);
                    }
                    if(list.size()>0){
                        //0为可回收、1为有害、2为厨余(湿)、3为其他(干)
                        WasteCategory wasteCategory = list.get(0);
                        String type = wasteCategory.getType();
                        if(type==null){
                            speekText(sessiongSpeechSynthesizerListener,noReusltStr[index]);
                        }else{
                            final String trashType = map.get(type);
                            if(!TextUtils.isEmpty(trashType)){
                                String text = name + "是" + trashType;
                                speekText(new BaseSpeechSynthesizerListener(TAG,"") {
                                    @Override
                                    public void onSynthesizeStart() {
                                        if(voiceLogStringBuffer!=null){
                                            voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                                            voiceLogStringBuffer.append( "准备开始合成,序列号\n");
                                        }
                                    }

                                    @Override
                                    public void onSynthesizeFinish() {
                                        if(voiceLogStringBuffer!=null){
                                            voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                                            voiceLogStringBuffer.append( "合成结束回调, 序列号\n");
                                        }
                                    }

                                    @Override
                                    public void onSpeechStart() {
                                        if(voiceLogStringBuffer!=null){
                                            voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                                            voiceLogStringBuffer.append( "播放开始回调, 序列号\n");
                                        }
                                    }

                                    @Override
                                    public void onSpeechProgress(String s, int progress) {
                                        //LogUtils.e(TAG,"s = " + s + " , progress = " + progress);
                                        if(cnAnswerListener!=null){
                                            cnAnswerListener.onSpeechProgress(s,tag,progress);
                                        }
                                    }

                                    @Override
                                    public void onSpeechFinished(String s) {
                                        if(voiceLogStringBuffer!=null){
                                            voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                                            voiceLogStringBuffer.append( "播放结束回调, 序列号\n");
                                        }
                                        if(cnAnswerListener!=null){
                                            cnAnswerListener.onPlayTip();
                                            cnAnswerListener.onTrashClassify(trashType);
                                            //cnAnswerListener.onEnd();
                                        }
                                        if(cnAnswerListener!=null){
                                            cnAnswerListener.onWakeUpEnd();
                                        }
                                        if(baiduRecUtil!=null){
                                            baiduRecUtil.releaseASR();
                                        }
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startListen();
                                            }
                                        },500);
                                    }

                                    @Override
                                    public void onError(String error, int code) {
                                        if(voiceLogStringBuffer!=null){
                                            voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                                            voiceLogStringBuffer.append( error + "\n");
                                        }
                                    }

                                }, text);
                            }
                        }
                    }
                }
            }
        };
        SubscriberOnErrorListener subscriberOnErrorListener = new SubscriberOnErrorListener() {
            @Override
            public void OnError(Throwable e) {
                LogUtils.e(e.toString());
                if(cnAnswerListener!=null){
                    LogUtils.e(TAG,"onEnd():垃圾分类查询error");
                    cnAnswerListener.onEnd();
                }
                end();
            }
        };
        ProgressSubscriber<ResponseResult<List<WasteCategory>>> progressSubscriber = new ProgressSubscriber<>(context,
                subscriberOnNextListener,subscriberOnErrorListener,false,"");
        NetworkManager.getInstance().classifyType(progressSubscriber,name);
    }

    public void batchSpeak(List<Pair<String, String>> list){
        baiduTTS.initTts(defaultSpeechSynthesizerListener);
        baiduTTS.batchSpeak(list);
    }

    public void speekText(String text){
        speekText(defaultSpeechSynthesizerListener,text);
    }

    public void onlySpeekText(String text){
        baiduTTS.speak(text);
    }

    private String realText;

    private void speekText(final BaseSpeechSynthesizerListener listener, final String text){
        LogUtils.e(TAG,"speek : " + text);
        realText = text;
        if(text.length()>=120){
            realText = text.substring(0,121);
        }
        this.tag = realText;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.e(TAG,"baiduTTS.initTts(listener)");
                baiduTTS.initTts(listener);
            }
        },20);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(voiceLogStringBuffer!=null){
                    voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                    voiceLogStringBuffer.append(text + "\n");
                }
                LogUtils.e(TAG,"baiduTTS.speak");
                baiduTTS.speak(realText);
            }
        },200);
        if(cnAnswerListener!=null){
            cnAnswerListener.onAnswer(text);
        }
    }

    private BaseSpeechSynthesizerListener sessiongSpeechSynthesizerListener = new BaseSpeechSynthesizerListener(TAG,"") {
        @Override
        public void onSynthesizeStart() {
            if(!isFirstNotUnderStand){
                return;
            }
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "准备开始合成,序列号\n");
            }
        }

        @Override
        public void onSynthesizeFinish() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "合成结束回调, 序列号\n");
            }
        }

        @Override
        public void onSpeechStart() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "播放开始回调, 序列号\n");
            }
        }

        @Override
        public void onSpeechProgress(String s, int progress) {
            //LogUtils.e(TAG,"tag = " + tag + " , s = " + s + " , progress = " + progress);
            if(cnAnswerListener!=null){
                cnAnswerListener.onSpeechProgress(s,tag,progress);
            }
        }

        @Override
        public void onSpeechFinished(String s) {
            if(cnAnswerListener!=null){
                cnAnswerListener.onPlayTip();
            }
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "播放结束回调, 序列号\n");
            }
            if(cnAnswerListener!=null){
                cnAnswerListener.onWakeUpEnd();
            }
            if(baiduRecUtil!=null){
                baiduRecUtil.releaseASR();
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startListen();
                }
            },500);
        }

        @Override
        public void onError(String error, int code) {
            if(isFirstNotUnderStand){
                isFirstNotUnderStand = false;
                String text = "我没有听清哟，可以虫说哦";
                if(voiceLogStringBuffer!=null){
                    voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                    voiceLogStringBuffer.append( text + "\n");
                }
                speekText(sessiongSpeechSynthesizerListener,text);
            }else{
                int index = new Random().nextInt(waitStr.length);
                if(cnAnswerListener!=null){
                    cnAnswerListener.onAnswer(waitStr[index]);
                }
                speekText(lastSpeechSynthesizerListener,waitStr[index]);
            }
        }
    };

    private BaseSpeechSynthesizerListener lastSpeechSynthesizerListener = new BaseSpeechSynthesizerListener(TAG,"lastSpeechSynthesizerListener") {
        @Override
        public void onSynthesizeStart() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "准备开始合成,序列号\n");
            }
        }

        @Override
        public void onSynthesizeFinish() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "合成结束回调, 序列号\n");
            }
        }

        @Override
        public void onSpeechStart() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "播放开始回调, 序列号\n");
            }
        }

        @Override
        public void onSpeechProgress(String s, int progress) {
            //LogUtils.e(TAG,"s = " + s + " , progress = " + progress);
            if(cnAnswerListener!=null){
                cnAnswerListener.onSpeechProgress(s,tag,progress);
            }
        }

        @Override
        public void onSpeechFinished(String s) {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "播放结束回调, 序列号\n");
            }
            if(cnAnswerListener!=null){
                LogUtils.e(TAG,"onEnd():lastSpeechSynthesizerListener===onSpeechFinished");
                cnAnswerListener.onEnd();
            }
            end();
        }

        @Override
        public void onError(String error, int code) {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( error + "\n");
            }
            if(cnAnswerListener!=null){
                LogUtils.e(TAG,"onEnd():lastSpeechSynthesizerListener===onError");
                cnAnswerListener.onEnd();
            }
            end();
        }
    };

    private BaseSpeechSynthesizerListener defaultSpeechSynthesizerListener = new BaseSpeechSynthesizerListener(TAG,"") {
        @Override
        public void onSynthesizeStart() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "准备开始合成,序列号\n");
            }
        }

        @Override
        public void onSynthesizeFinish() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "合成结束回调, 序列号\n");
            }
        }

        @Override
        public void onSpeechStart() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "播放开始回调, 序列号\n");
            }
        }

        @Override
        public void onSpeechProgress(String s, int progress) {

        }

        @Override
        public void onSpeechFinished(String s) {
            if(cnAnswerListener!=null){
                cnAnswerListener.onPlayTip();
                LogUtils.e(TAG,"onEnd():defaultSpeechSynthesizerListener===onSpeechFinished");
                cnAnswerListener.onEnd();
            }
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "播放结束回调, 序列号\n");
            }
        }

        @Override
        public void onError(String error, int code) {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( error + "\n");
            }
            if(cnAnswerListener!=null){
                LogUtils.e(TAG,"onEnd():defaultSpeechSynthesizerListener===onError");
                cnAnswerListener.onEnd();
            }
        }
    };

    private BaseSpeechSynthesizerListener volumeSpeechSynthesizerListener = new BaseSpeechSynthesizerListener(TAG,"volumeSpeechSynthesizerListener") {
        @Override
        public void onSynthesizeStart() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "准备开始合成,序列号\n");
            }
        }

        @Override
        public void onSynthesizeFinish() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "合成结束回调, 序列号\n");
            }
        }

        @Override
        public void onSpeechStart() {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "播放开始回调, 序列号\n");
            }
        }

        @Override
        public void onSpeechProgress(String s, int progress) {

        }

        @Override
        public void onSpeechFinished(String s) {
            if(cnAnswerListener!=null){
                cnAnswerListener.onPlayTip();
            }
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( "播放结束回调, 序列号\n");
            }
            if(baiduRecUtil!=null){
                baiduRecUtil.releaseASR();
            }
            if(cnAnswerListener!=null){
                cnAnswerListener.onWakeUpEnd();
            }
            final IRecogListener listener = new BaseRecogListener(TAG,"") {
                @Override
                public void onAsrError(int error, String desc) {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append(desc + "\n");
                    }
                    if(isVolumeNotUnderStand){
                        String text = "请说出正确的数字,设置范围是6到10,直接说数字即可设置音量";
                        speekText(volumeSpeechSynthesizerListener, text);
                        if(cnAnswerListener!=null){
                            cnAnswerListener.onAnswer(text);
                            cnAnswerListener.onWakeUpEnd();
                        }
                        isVolumeNotUnderStand = false;
                    }else{
                        int index = new Random().nextInt(waitStr.length);
                        speekText(lastSpeechSynthesizerListener,waitStr[index]);
                        if(cnAnswerListener!=null){
                            cnAnswerListener.onAnswer(waitStr[index]);
                        }
                    }
                }

                @Override
                public void onVoiceAsrReady() {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append("引擎就绪，可以开始说话\n");
                    }
                }

                @Override
                public void onVoiceAsrBegin() {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append("检测到用户说话\n");
                    }
                }

                @Override
                public void onVoiceAsrEnd() {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append("【asr.end事件】检测到用户说话结束\n");
                    }
                }

                @Override
                public void onAsrFinalResult(String result) {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append(result + "\n");
                    }
                    String voice = result.replace("，","").replace("。","");
                    if(cnAnswerListener!=null){
                        cnAnswerListener.onRecogText(voice);
                    }
                    int index = -1;
                    for(int i=0;i<volumes.length;i++){
                        if(voice.equals(volumes[i])){
                            index = i + 6;
                            break;
                        }
                    }
                    if(index!=-1){
                        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        double step = (double)maxVolume/10;
                        int volume = (int) (index * step);
                        LogUtils.e(TAG,"step = " + step +  ",volume = " + volume);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                        speekText(defaultSpeechSynthesizerListener, "音量设置完毕");
                    }else{
                        if(isVolumeNotUnderStand){
                            speekText(volumeSpeechSynthesizerListener, "请说出正确的数字,设置范围是6到10,直接说数字即可设置音量");
                            isVolumeNotUnderStand = false;
                        }else{
                            speekText(lastSpeechSynthesizerListener,waitStr[new Random().nextInt(waitStr.length)]);
                        }
                    }
                }

                @Override
                public void onAsrFinishError(String message) {
                    if(voiceLogStringBuffer!=null){
                        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                        voiceLogStringBuffer.append(message + "\n");
                    }
                    if(isVolumeNotUnderStand){
                        String text = "请说出正确的数字,设置范围是6到10,直接说数字即可设置音量";
                        speekText(volumeSpeechSynthesizerListener, text);
                        if(cnAnswerListener!=null){
                            cnAnswerListener.onAnswer(text);
                        }
                        isVolumeNotUnderStand = false;
                    }else{
                        int index = new Random().nextInt(waitStr.length);
                        speekText(lastSpeechSynthesizerListener,waitStr[index]);
                        if(cnAnswerListener!=null){
                            cnAnswerListener.onAnswer(waitStr[index]);
                        }
                    }
                }
            };
            if(baiduRecUtil!=null){
                baiduRecUtil.releaseASR();
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initBaduAsrUtil(context,listener);
                    baiduRecUtil.startASR();
                }
            },500);
        }

        @Override
        public void onError(String error, int code) {
            if(voiceLogStringBuffer!=null){
                voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                voiceLogStringBuffer.append( error + "\n");
            }
            if(cnAnswerListener!=null){
                LogUtils.e(TAG,"onEnd():volumeSpeechSynthesizerListener===onError");
                cnAnswerListener.onEnd();
            }
            end();
        }
    };

    private void startListen(){
        IRecogListener listener = new BaseRecogListener(TAG,"") {
            @Override
            public void onAsrError(int error, String desc) {
                if(voiceLogStringBuffer!=null){
                    voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                    voiceLogStringBuffer.append( error + "\n");
                }
                int index = new Random().nextInt(waitStr.length);
                if(cnAnswerListener!=null){
                    LogUtils.e(TAG,"onEnd():startListen===onAsrError");
                    //cnAnswerListener.onWakeUp();
                    cnAnswerListener.onEnd();
                    cnAnswerListener.onAnswer(waitStr[index]);
                }
                //speekText(lastSpeechSynthesizerListener,VOICE_THANKS);

                speekText(lastSpeechSynthesizerListener,waitStr[index]);
                end();
            }

            @Override
            public void onVoiceAsrReady() {
                if(voiceLogStringBuffer!=null){
                    voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                    voiceLogStringBuffer.append("引擎就绪，可以开始说话\n");
                }
            }

            @Override
            public void onVoiceAsrBegin() {
                if(voiceLogStringBuffer!=null){
                    voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                    voiceLogStringBuffer.append("检测到用户说话\n");
                }
            }

            @Override
            public void onVoiceAsrEnd() {
                if(voiceLogStringBuffer!=null){
                    voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                    voiceLogStringBuffer.append("【asr.end事件】检测到用户说话结束\n");
                }
            }

            @Override
            public void onAsrFinalResult(String result) {
                if(voiceLogStringBuffer!=null){
                    voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                    voiceLogStringBuffer.append(result + "\n");
                }
                String voice = result.replace("，","").replace("。","");
                if(cnAnswerListener!=null){
                    cnAnswerListener.onRecogText(voice);
                }
                getAnswer(sessionId,voice);
            }

            @Override
            public void onAsrFinishError(String message) {
                if(voiceLogStringBuffer!=null){
                    voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
                    voiceLogStringBuffer.append(message + "\n");
                }
                int index = new Random().nextInt(waitStr.length);
                if(cnAnswerListener!=null){
                    cnAnswerListener.onAnswer(waitStr[index]);
                    //cnAnswerListener.onWakeUp();
                }
                speekText(lastSpeechSynthesizerListener,waitStr[index]);
                end();
            }
        };
        initBaduAsrUtil(context,listener);
        baiduRecUtil.startASR();
    }

    public void end(){
        if(voiceLogStringBuffer==null){
            return;
        }
        voiceLogStringBuffer.append(DateUtil.getDateAndTime(DateUtil.STYLE11,System.currentTimeMillis()) + "");
        voiceLogStringBuffer.append( "结束\n");
        LogUtils.e(TAG,voiceLogStringBuffer.toString());
        final String path = LogUtils.writeLogtoFile(voiceLogStringBuffer.toString());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                upLoadFile(new File(path),sessionId);
            }
        },5000);
    }

    private void upLoadFile(File file, final String sn) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));

        RequestBody requestBody = builder.build();
        SubscriberOnNextListener<ResponseResult<String>> subscriberOnNextListener = new SubscriberOnNextListener<ResponseResult<String>>() {
            @Override
            public void onNext(ResponseResult<String> stringNetworklResult) {
                if (stringNetworklResult.getRetCode() == Api.CODE_SUCCESS) {
                    LogUtils.e(TAG,stringNetworklResult.getData());
                    trashLog(stringNetworklResult.getData(),sn);
                }
            }
        }
                ;
        SubscriberOnErrorListener subscriberOnErrorListener = new SubscriberOnErrorListener() {
            @Override
            public void OnError(Throwable e) {
                LogUtils.e(e.toString());
            }
        };
        ProgressSubscriber<ResponseResult<String>> progressSubscriber = new ProgressSubscriber<ResponseResult<String>>(context,
                subscriberOnNextListener, subscriberOnErrorListener, false, "");
        NetworkManager.getInstance().uploadFile(progressSubscriber, requestBody);
    }

    private void trashLog(String url,String sn){
        SubscriberOnNextListener<ResponseResult<Object>> subscriberOnNextListener = new SubscriberOnNextListener<ResponseResult<Object>>() {
            @Override
            public void onNext(ResponseResult<Object> objectResponseResult) {

            }
        };
        SubscriberOnErrorListener subscriberOnErrorListener = new SubscriberOnErrorListener() {
            @Override
            public void OnError(Throwable e) {
                LogUtils.e(e.toString());
            }
        };
        ProgressSubscriber<ResponseResult<Object>> progressSubscriber = new ProgressSubscriber<>(context,
                subscriberOnNextListener,subscriberOnErrorListener,false,"");
        NetworkManager.getInstance().trashLog(progressSubscriber,url,sn);
    }

}
