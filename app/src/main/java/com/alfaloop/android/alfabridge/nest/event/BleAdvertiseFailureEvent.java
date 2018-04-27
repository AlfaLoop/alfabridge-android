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
package com.alfaloop.android.alfabridge.nest.event;

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
