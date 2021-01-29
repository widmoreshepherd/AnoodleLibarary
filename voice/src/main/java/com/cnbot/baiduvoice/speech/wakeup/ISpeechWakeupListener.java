package com.cnbot.baiduvoice.speech.wakeup;

/**
 * Copyright (c) 2016--2021/1/6  Hunan Cnbot Co., Ltd. All Rights Reserved.
 *
 * @descriptoin 思必弛唤醒回调
 * @FileName: ISpeechWakeupListener.java
 * @author: dc
 * @date: 2021/1/6 9:01
 * @version: 1.0
 */

public interface ISpeechWakeupListener {
    void wakeupSuccess(String id);

    void wakeupFialError(String errorStr);

    void wakeupInitSuccess();

    void wakeupInitFial();

}
