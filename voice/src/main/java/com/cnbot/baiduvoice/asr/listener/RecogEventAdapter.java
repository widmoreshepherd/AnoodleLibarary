package com.cnbot.baiduvoice.asr.listener;

import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.cnbot.baiduvoice.asr.util.RecogResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The type Recog event adapter.
 *
 * @author Created by dj on 2019/8/29.
 */
public class RecogEventAdapter implements EventListener {

    private IRecogListener listener;

    private static final String TAG = "RecogEventAdapter";

    /**
     * Instantiates a new Recog event adapter.
     *
     * @param listener the listener
     */
    public RecogEventAdapter(IRecogListener listener) {
        this.listener = listener;
    }

    // DEMO集成第三步 开始回调事件
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String currentJson = params;
        String logMessage = "name:" + name + "; params:" + params;

        Log.d(TAG, logMessage);
        if (false) {
            // 可以调试，不需要后续逻辑
            return;
        }
        if (listener == null) {
            return;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LOADED)) {
            //离线命令词加载
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED)) {
            //离线命令词释放
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            listener.onAsrReady();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            listener.onAsrBegin();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            listener.onAsrEnd();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            RecogResult recogResult = RecogResult.parseJson(params);
            // 识别结果
            String[] results = recogResult.getResultsRecognition();
            if (recogResult.isFinalResult()) {
                // 最终识别结果，长语音每一句话会回调一次
                Log.e(TAG, "最终识别结果 :" + params);
                listener.onAsrFinalResult(results, recogResult);
            } else if (recogResult.isPartialResult()) {
                // 临时识别结果
                listener.onAsrPartialResult(results, recogResult);
            }

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束
            RecogResult recogResult = RecogResult.parseJson(params);
            Log.e(TAG, "识别结束 :" + params);
            if (recogResult.hasError()) {
                int errorCode = recogResult.getError();
                int subErrorCode = recogResult.getSubError();
                listener.onAsrFinishError(errorCode, subErrorCode, recogResult.getDesc(), recogResult);
            } else {
                listener.onAsrFinish(recogResult);
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH)) {
            listener.onAsrLongFinish(); // 长语音
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
            listener.onAsrExit();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_ERROR)) {
            RecogResult recogResult = RecogResult.parseJson(params);
            listener.onAsrError(recogResult.getError(), recogResult.getDesc());
        } /*else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_AUDIO)) {
            Log.e(TAG, "是否为data数据");
        }*/
    }

    private Volume parseVolumeJson(String jsonStr) {
        Volume vol = new Volume();
        vol.origalJson = jsonStr;
        try {
            JSONObject json = new JSONObject(jsonStr);
            vol.volumePercent = json.getInt("volume-percent");
            vol.volume = json.getInt("volume");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vol;
    }

    private class Volume {
        private int volumePercent = -1;
        private int volume = -1;
        private String origalJson;
    }

}
