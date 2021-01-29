package com.anoodle.webapi.listener;

public interface CnAnswerListener {

    void onAnswer(String answer);

    void onSpeechProgress(String s,String text, int progress);

    void onRecogText(String text);

    void onPlayTip();

    void onTrashClassify(String trashType);

    void onEnd();

    void onWakeUpEnd();

    void onWakeUp();
}
