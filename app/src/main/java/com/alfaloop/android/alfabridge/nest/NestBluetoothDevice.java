/**
 * © Copyright AlfaLoop Technology Co., Ltd. 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package com.alfaloop.android.alfabridge.nest;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class NestBluetoothDevice implements Serializable {

    private String hwid;
    private String macAddr;
    private String platform;
    private String platformType;
    private String dfuVersion;
    private String dfuName;
    private String dfuUpdateDate;
    private String dfuSignature;
    private String bridgeCode;
    private String firmwareVersion;
    private int dfuOpcode;
    private int dfuSignatureSize;
    private boolean isBind;
    private boolean isDfu;
    private boolean isAuthorized;

    private static final long serialVersionUID = -7060210544600464481L;

    private BluetoothDevice bleDevice;

    public NestBluetoothDevice(BluetoothDevice device) {
        this.bleDevice = device;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getDfuVersion() {
        return dfuVersion;
    }

    public void setDfuVersion(String dfuVersion) {
        this.dfuVersion = dfuVersion;
    }

    public String getDfuName() {
        return dfuName;
    }

    public void setDfuName(String dfuName) {
        this.dfuName = dfuName;
    }

    public String getDfuUpdateDate() {
        return dfuUpdateDate;
    }

    public void setDfuUpdateDate(String dfuUpdateDate) {
        this.dfuUpdateDate = dfuUpdateDate;
    }

    public int getDfuOpcode() {
        return dfuOpcode;
    }

    public void setDfuOpcode(int dfuOpcode) {
        this.dfuOpcode = dfuOpcode;
    }

    public String getDfuSignature() {
        return dfuSignature;
    }

    public void setDfuSignature(String dfuSignature) {
        this.dfuSignature = dfuSignature;
    }

    public int getDfuSignatureSize() {
        return dfuSignatureSize;
    }

    public void setDfuSignatureSize(int dfuSignatureSize) {
        this.dfuSignatureSize = dfuSignatureSize;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    public String getHwid() {
        return hwid;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

    public boolean isDfu() {
        return isDfu;
    }

    public void setDfu(boolean dfu) {
        isDfu = dfu;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getBridgeCode() {
        return bridgeCode;
    }

    public void setBridgeCode(String bridgeCode) {
        this.bridgeCode = bridgeCode;
    }

    public BluetoothDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BluetoothDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NestBluetoothDevice that = (NestBluetoothDevice) o;

        return bleDevice.equals(that.bleDevice);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + bleDevice.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NestBluetoothDevice{" +
                "hwid='" + hwid + '\'' +
                ", macAddr='" + macAddr + '\'' +
                ", platform='" + platform + '\'' +
                ", platformType='" + platformType + '\'' +
                ", dfuVersion='" + dfuVersion + '\'' +
                ", dfuName='" + dfuName + '\'' +
                ", dfuUpdateDate='" + dfuUpdateDate + '\'' +
                ", dfuSignature='" + dfuSignature + '\'' +
                ", bridgeCode='" + bridgeCode + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", dfuOpcode=" + dfuOpcode +
                ", dfuSignatureSize=" + dfuSignatureSize +
                ", isBind=" + isBind +
                ", isDfu=" + isDfu +
                ", isAuthorized=" + isAuthorized +
                ", bleDevice=" + bleDevice +
                '}';
    }
}
