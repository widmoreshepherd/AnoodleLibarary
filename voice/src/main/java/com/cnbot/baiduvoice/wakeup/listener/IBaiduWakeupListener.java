package com.cnbot.baiduvoice.wakeup.listener;

/**
 * 描述：
 * 作者：dc on 2018/12/8 17:26
 * 邮箱：597210600@qq.com
 */
public interface IBaiduWakeupListener {
    void wakeupSuccess(String id);

    void wakeupFialError(String errorStr);


}
