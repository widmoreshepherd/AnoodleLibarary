package com.anoodle.webapi.listener;

public interface DeviceControlListener {

    void onGetDeviceInfo();

    void onWifi();

    void onOpenDoor(String num);

    void onVersion();

    void onDeviceSN();

    void onTrashState();

    void onRestart();

}
