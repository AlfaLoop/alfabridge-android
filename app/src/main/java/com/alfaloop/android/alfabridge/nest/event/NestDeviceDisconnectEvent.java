package com.alfaloop.android.alfabridge.nest.event;

/**
 * Created by Chris on 2017/9/4.
 */

public class NestDeviceDisconnectEvent {
    private String hwid;
    private String macAddr;

    public NestDeviceDisconnectEvent(String hwid, String macAddr) {
        this.hwid = hwid;
        this.macAddr = macAddr;
    }

    public String getHwid() {
        return hwid;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    @Override
    public String toString() {
        return "NestDeviceDisconnectEvent{" +
                "hwid='" + hwid + '\'' +
                ", macAddr='" + macAddr + '\'' +
                '}';
    }
}
