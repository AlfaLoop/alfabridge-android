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
package com.alfaloop.android.alfabridge.db.model;

import android.support.annotation.Nullable;

import com.alfaloop.android.alfabridge.db.util.GreenConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.List;

@Entity
public class DeployRecord {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    @NotNull
    private String uuid;

    @NotNull
    private String name;
    @NotNull
    private String hexprogram;
    @NotNull
    private String type;
    @NotNull
    private Date updateDate;

    @Nullable
    private String sdkVersion;
    @Nullable
    private String version;
    @Nullable
    private String hexicon;
    @Nullable
    private String webUrl;
    @Nullable
    private String platformType;

    @Nullable
    @Convert(converter = GreenConverter.class, columnType = String.class)
    private List<String> initFiles;

    @Generated(hash = 306532148)
    public DeployRecord(Long id, @NotNull String uuid, @NotNull String name,
                        @NotNull String hexprogram, @NotNull String type, @NotNull Date updateDate,
                        String sdkVersion, String version, String hexicon, String webUrl,
                        String platformType, List<String> initFiles) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.hexprogram = hexprogram;
        this.type = type;
        this.updateDate = updateDate;
        this.sdkVersion = sdkVersion;
        this.version = version;
        this.hexicon = hexicon;
        this.webUrl = webUrl;
        this.platformType = platformType;
        this.initFiles = initFiles;
    }

    @Generated(hash = 1881516723)
    public DeployRecord() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexprogram() {
        return this.hexprogram;
    }

    public void setHexprogram(String hexprogram) {
        this.hexprogram = hexprogram;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHexicon() {
        return this.hexicon;
    }

    public void setHexicon(String hexicon) {
        this.hexicon = hexicon;
    }

    public String getWebUrl() {
        return this.webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getPlatformType() {
        return this.platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public List<String> getInitFiles() {
        return this.initFiles;
    }

    public void setInitFiles(List<String> initFiles) {
        this.initFiles = initFiles;
    }

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

}
