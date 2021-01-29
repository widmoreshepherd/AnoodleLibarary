package com.cnbot.baiduvoice.asr;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.cnbot.baiduvoice.asr.listener.IRecogListener;
import com.cnbot.baiduvoice.asr.listener.RecogEventAdapter;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The type Baidu rec util.
 *
 * @author Created by dj on 2019/08/29. <p> 百度语音识别工具类。
 */
public class BaiduRecUtil {
    private static final String TAG = BaiduRecUtil.class.getSimpleName();
    /**
     * SDK 内部核心 EventManager 类
     */
    private EventManager asrManager;
    /**
     * 回调事件
     */
    private RecogEventAdapter recogEventAdapter;
    /**
     * 回调到主程序
     */
    private IRecogListener iRecogListener;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 百度识别对象
     */
    private static BaiduRecUtil instance = null;
    /**
     * 是否被初始化，在release资源之前，只能new一个。
     */
    private static volatile boolean isInited = false;

    /**
     * Instantiates a new Baidu rec util.
     *
     * @param mContext the m context
     * @param listener the listener
     */
    public BaiduRecUtil(Context mContext, IRecogListener listener) {
        this.mContext = mContext;
        this.iRecogListener = listener;
    }

    /**
     * 获取百度识别对象
     *
     * @param context  the context
     * @param listener the listener
     * @return baidu rec util
     */
    /*public static BaiduRecUtil getInstance(Context context, IRecogListener listener){
        if(instance == null){
            synchronized (BaiduRecUtil.class){
                if(instance == null){
                    instance = new BaiduRecUtil(context, listener);

                }
            }
        }
        return instance;
    }*/


    /**
     * 初始化asr
     */
    public void initASR() {
       recogEventAdapter = new RecogEventAdapter(iRecogListener);
       if (isInited) {
           Log.e(TAG, "还未调用release()，请勿新建一个新类");
           throw new RuntimeException("还未调用release()，请勿新建一个新类");
       }
       isInited = true;
       asrManager = EventManagerFactory.create(mContext, "asr");
       asrManager.registerListener(recogEventAdapter);
   }

    /**
     * 开始识别
     */
    public void startASR() {
       //集成第二步，拼接识别参数，并开始识别
       if (!isInited) {
           throw new RuntimeException("release() was called");
       }
       Log.e(TAG , "开始识别");
       /**
        * Api的参数，仅用于生产调用START的json字符串，本身与SDK的调用无关。
        */
       Map<String ,Object> params = new LinkedHashMap<String, Object>();
       //普通话
       params.put(SpeechConstant.PID , 1537);
       params.put(SpeechConstant.VAD , SpeechConstant.VAD_DNN);
       /**
        * 0, 开启长语音（离线不支持）。建议pid选择15362
        * 800, 毫秒，静音800ms后断句，适用于输入短语
        * 2000, 毫秒，静音2000ms后断句，适用于输入长句
        * 2230, 毫秒，请修改代码中的VAD_ENDPOINT_TIMEOUT，自定义时长（建议800ms-3000ms之间）
         */
       params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT , 2000);
       //禁止音量回掉
       params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
       //保存音频文件
//       params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);
//       params.put(SpeechConstant.OUT_FILE, "/sdcard/outfile.pcm");
       // 识别音频文件
//       params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/baiduasr.pcm");
       String json = new JSONObject(params).toString();
       asrManager.send(SpeechConstant.ASR_START , json , null ,0 , 0);
   }

    /**
     * 提前结束录音等待识别结果
     */
    public void stopASR() {
       if (!isInited) {
           throw new RuntimeException("release() was called");
       }
       if (asrManager != null){
           Log.e(TAG , "停止识别");
           asrManager.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
       }
   }

    /**
     * 取消本次识别，取消后立即停止不会返回识别结果
     * cancel 与stop的区别是 cancel在stop的基础上，完全停止整个识别流程
     */
    public void cancelASR() {
       if (!isInited) {
           throw new RuntimeException("release() was called");
       }
       if (asrManager != null){
           Log.e(TAG , "停止识别");
           asrManager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
       }

   }

    /**
     * 描述：释放资源
     */
    public void releaseASR(){
        if (asrManager == null){
            return;
        }
        stopASR();
        asrManager.unregisterListener(recogEventAdapter);
        asrManager = null;
        isInited = false;
    }
}
