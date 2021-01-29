package com.cnbot.baiduvoice.asr.listener;


import com.cnbot.baiduvoice.asr.util.RecogResult;

/**
 * The interface Recog listener.
 *
 * @author Created by dj on 2019/09/10. 百度语义分析 与SDK中回调参数的对应关系定义在RecogEventAdapter类中
 */
public interface IRecogListener {

    /**
     * CALLBACK_EVENT_ASR_READY
     * ASR_START 输入事件调用后，引擎准备完毕
     */
    void onAsrReady();

    /**
     * CALLBACK_EVENT_ASR_BEGIN
     * onAsrReady后检查到用户开始说话
     */
    void onAsrBegin();

    /**
     * CALLBACK_EVENT_ASR_END
     * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
     */
    void onAsrEnd();

    /**
     * CALLBACK_EVENT_ASR_PARTIAL resultType=partial_result
     * onAsrBegin 后 随着用户的说话，返回的临时结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    void onAsrPartialResult(String[] results, RecogResult recogResult);

    /**
     * 不开启长语音仅回调一次，长语音的每一句话都会回调一次
     * CALLBACK_EVENT_ASR_PARTIAL resultType=final_result
     * 最终的识别结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    void onAsrFinalResult(String[] results, RecogResult recogResult);

    /**
     * CALLBACK_EVENT_ASR_FINISH
     *
     * @param recogResult 结束识别
     */
    void onAsrFinish(RecogResult recogResult);

    /**
     * CALLBACK_EVENT_ASR_FINISH error!=0
     *
     * @param errorCode    the error code
     * @param subErrorCode the sub error code
     * @param descMessage  the desc message
     * @param recogResult  the recog result
     */
    void onAsrFinishError(int errorCode, int subErrorCode, String descMessage,
                          RecogResult recogResult);

    /**
     * 长语音识别结束
     */
    void onAsrLongFinish();

    /**
     * CALLBACK_EVENT_ASR_EXIT
     * 引擎完成整个识别，空闲中
     */
    void onAsrExit();

    /**
     * CALLBACK_EVENT_ASR_ERROR
     *
     * @param error 错误码
     * @param desc  错误描述 引擎识别错误
     */
    void onAsrError(int error, String desc);

}
