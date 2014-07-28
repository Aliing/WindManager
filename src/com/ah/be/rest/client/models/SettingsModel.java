package com.ah.be.rest.client.models;

public class SettingsModel extends ResultModel {

    public String getHiveName() {
        return hiveName;
    }

    public void setHiveName(String hiveName) {
        this.hiveName = hiveName;
    }

    public short getModeType() {
        return modeType;
    }

    public void setModeType(short modeType) {
        this.modeType = modeType;
    }

    public String getSsidPwd() {
        return ssidPwd;
    }

    public void setSsidPwd(String ssidPwd) {
        this.ssidPwd = ssidPwd;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getAsciiKey() {
        return asciiKey;
    }

    public void setAsciiKey(String asciiKey) {
        this.asciiKey = asciiKey;
    }
    public String hiveName;
    public short modeType;
    public String ssidPwd;
    public String timeZone;
    public String asciiKey;
}
