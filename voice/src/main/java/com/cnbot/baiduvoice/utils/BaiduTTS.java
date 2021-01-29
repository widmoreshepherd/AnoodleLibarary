package com.cnbot.baiduvoice.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.cnbot.baiduvoice.control.InitConfig;
import com.cnbot.baiduvoice.control.MySyntherizer;
import com.cnbot.baiduvoice.listener.BaseSpeechSynthesizerListener;
import com.cnbot.baiduvoice.listener.MessageListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：
 * 作者：dc on 2018/1/24 09:03
 * 邮箱：597210600@qq.com
 */
public class BaiduTTS {
	private final String TAG = "BaiduTTS";

	/*protected String appId = "10733190";

	protected String appKey = "RRGE0rOZMgKb7zXNnsEpH7wG";

	protected String secretKey = "Qjfg9WVtuIFELvCDTtvKsBPUO7XjCGSp";*/

	protected String appId = "23561522";

	protected String appKey = "Wzdg43hEdZMMwBW6bobagtxX";

	protected String secretKey = "vYHaoONaWHrpzwkTDwlW56D2ggeDXLPA";

	// TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
	// TODO: 2018/4/28 离线合成必须先进行在线验证，百度配置包名必须与主模块build文件下的包名一致 
	protected TtsMode ttsMode = TtsMode.MIX;
	//修改离线合成音色
	protected String offlineVoice = OfflineResource.VOICE_FEMALE;

	// 主控制类，所有合成控制方法从这个类开始
	protected MySyntherizer synthesizer;

	private Context context;

	private static volatile BaiduTTS instanll = null;

	public static BaiduTTS getInstanll() {
		if (instanll == null) {
			synchronized (BaiduTTS.class) {
				if (instanll == null) {
					instanll = new BaiduTTS();
				}
			}
		}
		return instanll;
	}

	/**
	 * 建议采用子线程初始化
	 * @param context
	 */
	public void init(Context context) {
		this.context = context;
		initialTts();
	}

	/**
	 * 初始化引擎，需要的参数均在InitConfig类里
	 * <p>
	 * DEMO中提供了3个SpeechSynthesizerListener的实现
	 * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
	 * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
	 * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
	 */
	public void initialTts() {
		LoggerProxy.printable(true); // 日志打印在logcat中
		// 设置初始化参数
		// 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
		SpeechSynthesizerListener listener = new MessageListener();

		Map<String, String> params = getParams();

		// appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
		InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);

		// 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
		// 上线时请删除AutoCheck的调用
		/* AutoCheck.getInstance(context).check(initConfig, new Handler() {
		    @Override
		    public void handleMessage(Message msg) {
		        if (msg.what == 100) {
		            AutoCheck autoCheck = (AutoCheck) msg.obj;
		            synchronized (autoCheck) {
		                String message = autoCheck.obtainDebugMessage();
		                Log.w("AutoCheckMessage", message);
		            }
		        }
		    }
		
		});*/
		synthesizer = new MySyntherizer(context, initConfig); // 此处可以改为MySyntherizer 了解调用过程
	}

	public void initialTts(SpeechSynthesizerListener listener) {
		LoggerProxy.printable(true); // 日志打印在logcat中
		// 设置初始化参数
		Map<String, String> params = getParams();
		// appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
		InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
		synthesizer = new MySyntherizer(context, initConfig); // 此处可以改为MySyntherizer 了解调用过程
	}

    public void initTts(BaseSpeechSynthesizerListener listener) {
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        Map<String, String> params = getParams();
        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        synthesizer = new MySyntherizer(context, initConfig); // 此处可以改为MySyntherizer 了解调用过程
    }

	/**
	 * 合成的参数，可以初始化时填写，也可以在合成前设置。
	 *
	 * @return
	 */
	protected Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		// 以下参数均为选填
		// 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
		params.put(SpeechSynthesizer.PARAM_SPEAKER, "4");
		// 设置合成的音量，0-9 ，默认 5
		params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
		// 设置合成的语速，0-9 ，默认 5
		params.put(SpeechSynthesizer.PARAM_SPEED, "6");
		// 设置合成的语调，0-9 ，默认 5
		params.put(SpeechSynthesizer.PARAM_PITCH, "5");

		params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
		// 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
		// MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

		// 离线资源文件
		OfflineResource offlineResource = createOfflineResource(offlineVoice);
		// 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
		params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
		params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, offlineResource.getModelFilename());
		return params;
	}

	protected OfflineResource createOfflineResource(String voiceType) {
		OfflineResource offlineResource = null;
		try {
			offlineResource = new OfflineResource(context, voiceType);
		} catch (IOException e) {
			// IO 错误自行处理
			e.printStackTrace();
			Log.e(TAG, "【error】:copy files from assets failed." + e.getMessage());
		}
		return offlineResource;
	}

	/****************************************************************************************/

	/**
	 * speak 实际上是调用 synthesize后，获取音频流，然后播放。
	 * 获取音频流的方式见SaveFileActivity及FileSaveListener
	 * 需要合成的文本text的长度不能超过1024个GBK字节。
	 */
	public void speak(String text) {
		// 需要合成的文本text的长度不能超过1024个GBK字节。
		if (!TextUtils.isEmpty(text)) {
			// 合成前可以修改参数：
			// Map<String, String> params = getParams();
			// synthesizer.setParams(params);
			// TODO: 2018/3/8 加入非空判断
			if (synthesizer != null) {
				synthesizer.stop();
				int result = synthesizer.speak(text);
				checkResult(result, "speak");
			}
		}
	}

    public void batchSpeak(List<Pair<String, String>> list) {
        if (synthesizer != null) {
            synthesizer.stop();
            int result = synthesizer.batchSpeak(list);
            checkResult(result, "speak");
        }
    }

	/**
	 * 暂停播放。仅调用speak后生效
	 */
	public void pause() {
		if (null != synthesizer) {
			int result = synthesizer.pause();
			checkResult(result, "pause");
		}
	}

	/**
	 * 继续播放。仅调用speak后生效，调用pause生效
	 */
	public void resume() {
		int result = synthesizer.resume();
		checkResult(result, "resume");
	}

	/*
	 * 停止合成引擎。即停止播放，合成，清空内部合成队列。
	 */
	public void stop() {
		if (synthesizer != null) {
			int result = synthesizer.stop();
			checkResult(result, "stop");
		}
	}

	/**
	* 销毁。
	*/
	public void release() {
		if (synthesizer != null) {
			synthesizer.release();
		}
	}

	public void checkResult(int result, String method) {
		if (result != 0) {
			//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
			Log.e(TAG, "语音合成 error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
		}
	}

}
