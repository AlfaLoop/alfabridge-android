package com.alfaloop.android.alfabridge.nest.event;

/**
 * Created by Chris on 2017/11/9.
 */

public class BleAdvertiseFailureEvent {
    String errMessage;
    int errCode;

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public BleAdvertiseFailureEvent(String errMessage, int errCode) {
        this.errMessage = errMessage;
        this.errCode = errCode;
    }

    @Override
    public String toString() {
        return "BleAdvertiseFailureEvent{" +
                "errMessage='" + errMessage + '\'' +
                ", errCode=" + errCode +
                '}';
    }
}
